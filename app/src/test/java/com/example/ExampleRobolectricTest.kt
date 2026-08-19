package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  private lateinit var database: AppDatabase
  private lateinit var repository: ShopRepository
  private lateinit var context: Context

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    repository = ShopRepository(database.shopDao(), context)
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `read app string from context`() {
    val appName = context.getString(R.string.app_name)
    assertEquals("ShopInventory POS", appName)
  }

  @Test
  fun `initialize shop and seed 10 sample laptops`() = runBlocking {
    repository.initializeShop(
      shopName = "LOGIN GADGET",
      ownerEmail = "preamleelapranto@gmail.com",
      branch1Name = "Branch 1 - Main City Hub",
      branch2Name = "Branch 2 - Downtown Outlet",
      currencySymbol = "$",
      prefillSampleLaptops = true
    )

    val profile = repository.shopProfile.first()
    assertNotNull(profile)
    assertEquals("LOGIN GADGET", profile?.shopName)
    assertEquals("preamleelapranto@gmail.com", profile?.ownerEmail)

    val branches = repository.allBranches.first()
    assertEquals(2, branches.size)

    val units = repository.inStockInventoryUnits.first()
    assertEquals(10, units.size)

    // Check condition counts: 3 brand new, 3 used, 3 open box, 1 special unit
    val brandNew = units.count { it.condition == "Brand New" }
    val used = units.count { it.condition == "Used" }
    val openBox = units.count { it.condition == "Open Box" }
    val special = units.count { it.condition == "Special Unit" }

    assertEquals(3, brandNew)
    assertEquals(3, used)
    assertEquals(3, openBox)
    assertEquals(1, special)
  }

  @Test
  fun `sell unit removes from in stock and records sale with profit`() = runBlocking {
    repository.initializeShop(
      shopName = "LOGIN GADGET",
      ownerEmail = "preamleelapranto@gmail.com",
      branch1Name = "Branch 1 - Main City Hub",
      branch2Name = "Branch 2 - Downtown Outlet",
      currencySymbol = "$",
      prefillSampleLaptops = true
    )

    val initialInStock = repository.inStockInventoryUnits.first()
    val targetUnit = initialInStock.first()

    val invoice = repository.sellInventoryUnit(
      unitId = targetUnit.id,
      customerName = "David Miller",
      customerPhone = "+1 555-0199",
      customerAddress = "104 Tech Boulevard",
      sellingPrice = targetUnit.suggestedSellingPrice,
      paymentType = "ONLINE",
      bankingAppName = "bKash / Stripe",
      serviceWarrantyYears = 2.0,
      replacementWarrantyDays = 15,
      warrantyTerms = "Standard Hardware Warranty"
    )

    assertNotNull(invoice)
    assertEquals("David Miller", invoice.customerName)
    assertEquals(targetUnit.serialNumber, invoice.serialNumber)

    // In stock units must now be 9
    val remainingStock = repository.inStockInventoryUnits.first()
    assertEquals(9, remainingStock.size)
    assertTrue(remainingStock.none { it.id == targetUnit.id })

    // Sales history should contain 1 record
    val sales = repository.allSaleRecords.first()
    assertEquals(1, sales.size)
    assertEquals(invoice.invoiceNumber, sales.first().invoiceNumber)
  }
}
