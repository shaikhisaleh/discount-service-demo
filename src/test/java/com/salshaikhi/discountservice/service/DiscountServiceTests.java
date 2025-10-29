package com.salshaikhi.discountservice.service;

import com.salshaikhi.discountservice.dto.BillDto;
import com.salshaikhi.discountservice.dto.DiscountRequest;
import com.salshaikhi.discountservice.dto.DiscountResponse;
import com.salshaikhi.discountservice.dto.ItemDto;
import com.salshaikhi.discountservice.entity.Discount;
import com.salshaikhi.discountservice.entity.FlatRateDiscount;
import com.salshaikhi.discountservice.entity.PercentageBasedDiscount;
import com.salshaikhi.discountservice.entity.enums.DiscountType;
import com.salshaikhi.discountservice.entity.enums.UserType;
import com.salshaikhi.discountservice.exception.DuplicateCodeException;
import com.salshaikhi.discountservice.exception.NotFoundException;
import com.salshaikhi.discountservice.mapper.DiscountMapper;
import com.salshaikhi.discountservice.repository.DiscountRepository;
import com.salshaikhi.discountservice.security.SecurityUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DiscountServiceTests {
    @Mock
    private DiscountRepository discountRepository;
    @Mock
    private DiscountMapper discountMapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private DiscountService discountService;

    private static MockedStatic<SecurityUtil> mockedStatic;
    private static com.salshaikhi.discountservice.entity.User staticTestUser;

    @BeforeAll
    static void setUpUserMock() {
        String email = "testuser@example.com";
        UserType userType = UserType.EMPLOYEE;
        Instant createdAt = Instant.now().minusSeconds(60 * 60 * 24 * 365 * 3); // 3 years ago
        staticTestUser = new com.salshaikhi.discountservice.entity.User();
        staticTestUser.setEmail(email);
        staticTestUser.setUserType(userType);
        staticTestUser.setCreatedAt(createdAt);
        mockedStatic = mockStatic(SecurityUtil.class);
        mockedStatic.when(SecurityUtil::getUserEmail).thenReturn(email);
    }

    @BeforeEach
    void setupUserServiceMock() {
        when(userService.getByEmail("testuser@example.com")).thenReturn(staticTestUser);
    }

    @Test
    void testGetAll_withNoDiscounts_returnsEmptyList() {
        // Arrange
        final List<Discount> expectedDiscounts = Collections.emptyList();
        when(discountRepository.findAll()).thenReturn(expectedDiscounts);
        // Act
        List<DiscountResponse> result = discountService.getAll();
        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAll_withDiscounts_returnsList() {
        // Arrange
        final String expectedDiscountId1 = "id1";
        final FlatRateDiscount discount1 = new FlatRateDiscount();
        discount1.setId(expectedDiscountId1);
        final DiscountResponse response1 = new DiscountResponse();
        when(discountMapper.discountToResponse(discount1)).thenReturn(response1);

        final String expectedDiscountId2 = "id2";
        final PercentageBasedDiscount discount2 = new PercentageBasedDiscount();
        discount2.setId(expectedDiscountId2);
        final DiscountResponse response2 = new DiscountResponse();
        when(discountMapper.discountToResponse(discount2)).thenReturn(response2);

        final List<Discount> expectedDiscounts = Arrays.asList(discount1, discount2);
        when(discountRepository.findAll()).thenReturn(expectedDiscounts);
        // Act
        List<DiscountResponse> result = discountService.getAll();
        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(response1));
        assertTrue(result.contains(response2));
    }

    @Test
    void testGetOrNotFound_withExistingDiscount_returnsDiscount() {
        // Arrange
        final String expectedDiscountId = "id";
        final String expectedCode = "CODE123";
        final String expectedDescription = "Test discount";
        final double expectedAmount = 15.5;
        final boolean expectedActive = true;
        final Instant expectedExpiryDate = Instant.parse("2025-12-31T23:59:59Z");
        final Instant expectedCreatedAt = Instant.parse("2025-01-01T00:00:00Z");
        final Instant expectedUpdatedAt = Instant.parse("2025-10-29T00:00:00Z");

        final FlatRateDiscount expectedDiscount = new FlatRateDiscount();
        expectedDiscount.setId(expectedDiscountId);
        expectedDiscount.setCode(expectedCode);
        expectedDiscount.setDescription(expectedDescription);
        expectedDiscount.setAmount(expectedAmount);
        expectedDiscount.setActive(expectedActive);
        expectedDiscount.setExpiryDate(expectedExpiryDate);
        expectedDiscount.setCreatedAt(expectedCreatedAt);
        expectedDiscount.setUpdatedAt(expectedUpdatedAt);
        expectedDiscount.setMinAccountAgeYears(2);
        expectedDiscount.setPerAmountSpent(100.0);

        when(discountRepository.findById(expectedDiscountId)).thenReturn(Optional.of(expectedDiscount));
        // Act
        Discount result = discountService.getOrNotFound(expectedDiscountId);
        // Assert
        assertNotNull(result);
        assertEquals(expectedDiscountId, result.getId());
        assertEquals(expectedCode, result.getCode());
        assertEquals(expectedDescription, result.getDescription());
        assertEquals(expectedAmount, result.getAmount());
        assertEquals(expectedActive, result.isActive());
        assertEquals(expectedExpiryDate, result.getExpiryDate());
        assertEquals(expectedCreatedAt, result.getCreatedAt());
        assertEquals(expectedUpdatedAt, result.getUpdatedAt());
        assertTrue(result instanceof FlatRateDiscount);
        FlatRateDiscount flatRate = (FlatRateDiscount) result;
        assertEquals(2, flatRate.getMinAccountAgeYears());
        assertEquals(100.0, flatRate.getPerAmountSpent());
    }

    @Test
    void testCreatePercentageBasedDiscount_withValidRequest_discountCreatedSuccessfully() {
        // Arrange
        final String expectedCode = "EMPLOYEE30";
        final DiscountRequest request = new DiscountRequest();
        request.setCode(expectedCode);
        request.setDescription("30% off for employees");
        request.setAmount(30.0);
        request.setDiscountType(DiscountType.PERCENTAGE_BASED);
        request.setActive(true);
        request.setExpiryDate(Instant.now().plusSeconds(86400)); // 1 day from now
        request.setUserType(UserType.EMPLOYEE);
        request.setExcludedCategories(Set.of("Groceries"));

        final PercentageBasedDiscount expectedDiscount = new PercentageBasedDiscount();
        expectedDiscount.setCode(expectedCode);
        expectedDiscount.setUserType(UserType.EMPLOYEE);
        expectedDiscount.setExcludedCategories(Set.of("Groceries"));

        final DiscountResponse expectedResponse = new DiscountResponse();
        expectedResponse.setCode(expectedCode);
        expectedResponse.setDiscountType(DiscountType.PERCENTAGE_BASED);

        when(discountRepository.existsByCodeIgnoreCase(expectedCode)).thenReturn(false);
        when(discountRepository.save(any(PercentageBasedDiscount.class))).thenReturn(expectedDiscount);
        when(discountMapper.discountToResponse(any(Discount.class))).thenReturn(expectedResponse);
        when(discountMapper.requestToDiscount(eq(request), any(PercentageBasedDiscount.class))).thenReturn(expectedDiscount);

        // Act
        DiscountResponse result = discountService.create(request);

        // Assert
        assertEquals(expectedResponse, result);
        verify(discountRepository).save(any(PercentageBasedDiscount.class));
        verify(discountMapper).requestToDiscount(eq(request), any(PercentageBasedDiscount.class));
        verify(discountMapper).discountToResponse(expectedDiscount);
    }

    @Test
    void testCreateFlatRateDiscount_withValidRequest_discountCreatedSuccessfully() {
        // Arrange
        final String expectedCode = "FLAT5";
        final DiscountRequest request = new DiscountRequest();
        request.setCode(expectedCode);
        request.setDescription("$5 off for every $100 spent");
        request.setAmount(5.0);
        request.setDiscountType(DiscountType.FLAT_RATE);
        request.setActive(true);
        request.setExpiryDate(Instant.now().plusSeconds(86400)); // 1 day from now
        request.setMinAccountAgeYears(2);
        request.setPerAmountSpent(100.0);

        final FlatRateDiscount expectedDiscount = new FlatRateDiscount();
        expectedDiscount.setCode(expectedCode);
        expectedDiscount.setMinAccountAgeYears(2);
        expectedDiscount.setPerAmountSpent(100.0);

        final DiscountResponse expectedResponse = new DiscountResponse();
        expectedResponse.setCode(expectedCode);
        expectedResponse.setDiscountType(DiscountType.FLAT_RATE);

        when(discountRepository.existsByCodeIgnoreCase(expectedCode)).thenReturn(false);
        when(discountRepository.save(any(FlatRateDiscount.class))).thenReturn(expectedDiscount);
        when(discountMapper.discountToResponse(any(Discount.class))).thenReturn(expectedResponse);
        when(discountMapper.requestToDiscount(eq(request), any(FlatRateDiscount.class))).thenReturn(expectedDiscount);

        // Act
        DiscountResponse result = discountService.create(request);

        // Assert
        assertEquals(expectedResponse, result);
        verify(discountRepository).save(any(FlatRateDiscount.class));
        verify(discountMapper).requestToDiscount(eq(request), any(FlatRateDiscount.class));
        verify(discountMapper).discountToResponse(expectedDiscount);
    }

    @Test
    void testCreateDiscount_withDuplicateCode_throwsDuplicateCodeException() {
        // Arrange
        final String duplicateCode = "DUPLICATE";
        final DiscountRequest request = mock(DiscountRequest.class);
        when(request.getCode()).thenReturn(duplicateCode);
        when(discountRepository.existsByCodeIgnoreCase(duplicateCode)).thenReturn(true);
        // Act & Assert
        assertThrows(DuplicateCodeException.class, () -> discountService.create(request));
    }

    @Test
    void testUpdateDiscount_withValidRequest_discountUpdatedSuccessfully() {
        // Arrange
        final String expectedDiscountId = "id";
        final DiscountRequest request = mock(DiscountRequest.class);
        final FlatRateDiscount expectedDiscount = new FlatRateDiscount();
        final DiscountResponse expectedResponse = new DiscountResponse();
        when(discountRepository.findById(expectedDiscountId)).thenReturn(Optional.of(expectedDiscount));
        when(discountRepository.save(any(Discount.class))).thenReturn(expectedDiscount);
        when(discountMapper.discountToResponse(any(Discount.class))).thenReturn(expectedResponse);
        doAnswer(invocation -> null).when(discountMapper).requestToDiscount(any(), any());
        // Act
        DiscountResponse result = discountService.update(expectedDiscountId, request);
        // Assert
        assertEquals(expectedResponse, result);
    }

    @Test
    void testDeleteDiscount_withValidId_discountDeletedSuccessfully() {
        // Arrange
        final String expectedDiscountId = "id";
        doNothing().when(discountRepository).deleteById(expectedDiscountId);
        // Act
        discountService.delete(expectedDiscountId);
        // Assert
        verify(discountRepository, times(1)).deleteById(expectedDiscountId);
    }

    @Test
    void testGetOrNotFound_withNonExistingDiscount_throwsNotFoundException() {
        // Arrange
        final String nonExistingId = "not_found";
        when(discountRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(NotFoundException.class, () -> discountService.getOrNotFound(nonExistingId));
    }

    @Test
    void testApplyDiscount_withFlatAndPercentDiscounts_discountAppliedSuccessfully() {
        // Arrange
        final String expectedCategoryElectronics = "electronics";
        final String expectedCategoryGrocery = "grocery";
        final double expectedItem1Price = 100.0;
        final int expectedItem1Quantity = 2;
        final double expectedItem2Price = 50.0;
        final int expectedItem2Quantity = 1;
        final String expectedFlatDiscountCode = "FLAT10";
        final double expectedFlatDiscountAmount = 5.0;
        final double expectedFlatDiscountPerAmount = 100.0;
        final String expectedPercentDiscountCode = "EMP20";
        final double expectedPercentDiscountAmount = 30.0;
        final String expectedExcludedCategory = expectedCategoryGrocery;
        final UserType expectedUserType = UserType.EMPLOYEE;
        final double expectedTotalPrice = 250.0;
        final long expectedExpirySeconds = 3600L;

        ItemDto item1 = new ItemDto();
        item1.setCategory(expectedCategoryElectronics);
        item1.setPrice(expectedItem1Price);
        item1.setQuantity(expectedItem1Quantity);
        ItemDto item2 = new ItemDto();
        item2.setCategory(expectedCategoryGrocery);
        item2.setPrice(expectedItem2Price);
        item2.setQuantity(expectedItem2Quantity);
        BillDto bill = new BillDto();
        bill.setItems(Arrays.asList(item1, item2));
        bill.setAppliedDiscounts(new ArrayList<>());

        FlatRateDiscount flatDiscount = new FlatRateDiscount();
        flatDiscount.setCode(expectedFlatDiscountCode);
        flatDiscount.setAmount(expectedFlatDiscountAmount);
        flatDiscount.setActive(true);
        flatDiscount.setExpiryDate(Instant.now().plusSeconds(expectedExpirySeconds));
        flatDiscount.setPerAmountSpent(expectedFlatDiscountPerAmount);
        flatDiscount.setMinAccountAgeYears(2);

        PercentageBasedDiscount percentDiscount = new PercentageBasedDiscount();
        percentDiscount.setCode(expectedPercentDiscountCode);
        percentDiscount.setAmount(expectedPercentDiscountAmount);
        percentDiscount.setActive(true);
        percentDiscount.setExpiryDate(Instant.now().plusSeconds(expectedExpirySeconds));
        percentDiscount.setUserType(expectedUserType);
        percentDiscount.setExcludedCategories(Set.of(expectedExcludedCategory));

        when(discountRepository.findAllFlatRateDiscounts()).thenReturn(Arrays.asList(flatDiscount));
        when(discountRepository.findAllPercentageBasedDiscounts()).thenReturn(Arrays.asList(percentDiscount));

        // Act
        BillDto result = discountService.applyDiscount(bill);

        // Assert
        assertEquals(expectedTotalPrice, result.getTotalPrice());
        // Flat: $5 per $100, so $10. Percent: 30% on electronics only (200*0.3 = $60). Total discount: $70
        assertEquals(180.0, result.getPriceAfterDiscount());
        assertTrue(result.getAppliedDiscounts().contains(expectedFlatDiscountCode));
        assertTrue(result.getAppliedDiscounts().contains(expectedPercentDiscountCode));
    }

    @Test
    void testApplyDiscount_withNoApplicableDiscounts_noDiscountApplied() {
        // Arrange
        final String expectedCategoryGrocery = "grocery";
        final double expectedItemPrice = 50.0;
        final int expectedItemQuantity = 1;
        final double expectedTotalPrice = 50.0;
        final double expectedPriceAfterDiscount = 50.0;

        ItemDto item = new ItemDto();
        item.setCategory(expectedCategoryGrocery);
        item.setPrice(expectedItemPrice);
        item.setQuantity(expectedItemQuantity);
        BillDto bill = new BillDto();
        bill.setItems(List.of(item));
        bill.setAppliedDiscounts(new ArrayList<>());
        when(discountRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        BillDto result = discountService.applyDiscount(bill);

        // Assert
        assertEquals(expectedTotalPrice, result.getTotalPrice());
        assertEquals(expectedPriceAfterDiscount, result.getPriceAfterDiscount());
        assertTrue(result.getAppliedDiscounts().isEmpty());
    }

    @Test
    void testApplyDiscount_withMultiplePercentDiscounts_appliesHighestPercent() {
        // Arrange
        final String expectedCategoryElectronics = "electronics";
        final double expectedItem1Price = 100.0;
        final int expectedItem1Quantity = 2;
        final String expectedPercentDiscountCode1 = "EMP10";
        final double expectedPercentDiscountAmount1 = 10.0;
        final String expectedPercentDiscountCode2 = "EMP30";
        final double expectedPercentDiscountAmount2 = 30.0;
        final UserType expectedUserType = UserType.EMPLOYEE;
        final double expectedTotalPrice = 200.0;
        // Only the highest percent (30%) should apply: 200 * 0.3 = 60
        final double expectedPriceAfterDiscount = 140.0;
        final Instant expectedExpiryDate = Instant.parse("2026-12-31T23:59:59Z");

        ItemDto item1 = new ItemDto();
        item1.setCategory(expectedCategoryElectronics);
        item1.setPrice(expectedItem1Price);
        item1.setQuantity(expectedItem1Quantity);
        BillDto bill = new BillDto();
        bill.setItems(Arrays.asList(item1));
        bill.setAppliedDiscounts(new ArrayList<>());

        PercentageBasedDiscount percentDiscount1 = new PercentageBasedDiscount();
        percentDiscount1.setCode(expectedPercentDiscountCode1);
        percentDiscount1.setAmount(expectedPercentDiscountAmount1);
        percentDiscount1.setActive(true);
        percentDiscount1.setExpiryDate(expectedExpiryDate);
        percentDiscount1.setUserType(expectedUserType);

        PercentageBasedDiscount percentDiscount2 = new PercentageBasedDiscount();
        percentDiscount2.setCode(expectedPercentDiscountCode2);
        percentDiscount2.setAmount(expectedPercentDiscountAmount2);
        percentDiscount2.setActive(true);
        percentDiscount2.setExpiryDate(expectedExpiryDate);
        percentDiscount2.setUserType(expectedUserType);

        when(discountRepository.findAllPercentageBasedDiscounts()).thenReturn(Arrays.asList(percentDiscount1, percentDiscount2));

        // Act
        BillDto result = discountService.applyDiscount(bill);

        // Assert
        assertEquals(expectedTotalPrice, result.getTotalPrice());
        assertEquals(expectedPriceAfterDiscount, result.getPriceAfterDiscount());
        assertFalse(result.getAppliedDiscounts().contains(expectedPercentDiscountCode1));
        assertTrue(result.getAppliedDiscounts().contains(expectedPercentDiscountCode2));
    }

    @Test
    void testApplyDiscount_withMultipleFlatDiscounts_appliesAllFlats() {
        // Arrange
        final String expectedCategoryElectronics = "electronics";
        final double expectedItem1Price = 100.0;
        final int expectedItem1Quantity = 2;
        final String expectedFlatDiscountCode1 = "FLAT5";
        final double expectedFlatDiscountAmount1 = 5.0;
        final double expectedFlatDiscountPerAmount1 = 100.0;
        final String expectedFlatDiscountCode2 = "FLAT10";
        final double expectedFlatDiscountAmount2 = 10.0;
        final double expectedFlatDiscountPerAmount2 = 200.0;
        final double expectedTotalPrice = 200.0;
        // FLAT5: $5 per $100, so $10. FLAT10: $10 per $200, so $10. Total discount: $20
        final double expectedPriceAfterDiscount = 180.0;
        final long expectedExpirySeconds = 3600L;

        ItemDto item1 = new ItemDto();
        item1.setCategory(expectedCategoryElectronics);
        item1.setPrice(expectedItem1Price);
        item1.setQuantity(expectedItem1Quantity);
        BillDto bill = new BillDto();
        bill.setItems(Arrays.asList(item1));
        bill.setAppliedDiscounts(new ArrayList<>());

        FlatRateDiscount flatDiscount1 = new FlatRateDiscount();
        flatDiscount1.setCode(expectedFlatDiscountCode1);
        flatDiscount1.setAmount(expectedFlatDiscountAmount1);
        flatDiscount1.setActive(true);
        flatDiscount1.setExpiryDate(Instant.now().plusSeconds(expectedExpirySeconds));
        flatDiscount1.setPerAmountSpent(expectedFlatDiscountPerAmount1);
        flatDiscount1.setMinAccountAgeYears(2);

        FlatRateDiscount flatDiscount2 = new FlatRateDiscount();
        flatDiscount2.setCode(expectedFlatDiscountCode2);
        flatDiscount2.setAmount(expectedFlatDiscountAmount2);
        flatDiscount2.setActive(true);
        flatDiscount2.setExpiryDate(Instant.now().plusSeconds(expectedExpirySeconds));
        flatDiscount2.setPerAmountSpent(expectedFlatDiscountPerAmount2);
        flatDiscount2.setMinAccountAgeYears(2);

        when(discountRepository.findAllFlatRateDiscounts()).thenReturn(Arrays.asList(flatDiscount1, flatDiscount2));

        // Act
        BillDto result = discountService.applyDiscount(bill);

        // Assert
        assertEquals(expectedTotalPrice, result.getTotalPrice());
        assertEquals(expectedPriceAfterDiscount, result.getPriceAfterDiscount());
        assertTrue(result.getAppliedDiscounts().contains(expectedFlatDiscountCode1));
        assertTrue(result.getAppliedDiscounts().contains(expectedFlatDiscountCode2));
    }

    @Test
    void testApplyDiscount_withAllItemsExcludedCategory_noPercentDiscountApplied() {
        // Arrange
        final String expectedCategoryGrocery = "grocery";
        final double expectedItem1Price = 100.0;
        final int expectedItem1Quantity = 2;
        final double expectedItem2Price = 50.0;
        final int expectedItem2Quantity = 1;
        final String expectedPercentDiscountCode = "EMP30";
        final double expectedPercentDiscountAmount = 30.0;
        final UserType expectedUserType = UserType.EMPLOYEE;
        final double expectedTotalPrice = 250.0;
        // No discounts should apply, as all items are excluded from percent and there are no flat discounts
        final double expectedPriceAfterDiscount = 250.0;
        final long expectedExpirySeconds = 3600L;

        ItemDto item1 = new ItemDto();
        item1.setCategory(expectedCategoryGrocery);
        item1.setPrice(expectedItem1Price);
        item1.setQuantity(expectedItem1Quantity);
        ItemDto item2 = new ItemDto();
        item2.setCategory(expectedCategoryGrocery);
        item2.setPrice(expectedItem2Price);
        item2.setQuantity(expectedItem2Quantity);
        BillDto bill = new BillDto();
        bill.setItems(Arrays.asList(item1, item2));
        bill.setAppliedDiscounts(new ArrayList<>());

        PercentageBasedDiscount percentDiscount = new PercentageBasedDiscount();
        percentDiscount.setCode(expectedPercentDiscountCode);
        percentDiscount.setAmount(expectedPercentDiscountAmount);
        percentDiscount.setActive(true);
        percentDiscount.setExpiryDate(Instant.now().plusSeconds(expectedExpirySeconds));
        percentDiscount.setUserType(expectedUserType);
        percentDiscount.setExcludedCategories(Set.of(expectedCategoryGrocery));

        when(discountRepository.findAll()).thenReturn(List.of(percentDiscount));

        // Act
        BillDto result = discountService.applyDiscount(bill);

        // Assert
        assertEquals(expectedTotalPrice, result.getTotalPrice());
        assertEquals(expectedPriceAfterDiscount, result.getPriceAfterDiscount());
        assertTrue(result.getAppliedDiscounts().isEmpty());
    }

    @Test
    void testGetDiscountResponse_withExistingDiscount_returnsDiscountResponse() {
        // Arrange
        final String discountId = "id";
        final FlatRateDiscount discount = new FlatRateDiscount();
        final DiscountResponse response = new DiscountResponse();
        when(discountRepository.findById(discountId)).thenReturn(Optional.of(discount));
        when(discountMapper.discountToResponse(discount)).thenReturn(response);
        // Act
        DiscountResponse result = discountService.getDiscountResponse(discountId);
        // Assert
        assertEquals(response, result);
    }

    @Test
    void testApplyDiscount_withOnlyFlatDiscounts_appliesFlatDiscounts() {
        // Arrange
        final String expectedCategory = "electronics";
        final double expectedItemPrice = 100.0;
        final int expectedItemQuantity = 2;
        final String expectedFlatDiscountCode = "FLAT10";
        final double expectedFlatDiscountAmount = 10.0;
        final double expectedFlatDiscountPerAmount = 100.0;
        final double expectedTotalPrice = 200.0;
        final double expectedPriceAfterDiscount = 180.0; // $10 per $100, so $20 off
        final long expectedExpirySeconds = 3600L;

        ItemDto item = new ItemDto();
        item.setCategory(expectedCategory);
        item.setPrice(expectedItemPrice);
        item.setQuantity(expectedItemQuantity);
        BillDto bill = new BillDto();
        bill.setItems(List.of(item));
        bill.setAppliedDiscounts(new ArrayList<>());

        FlatRateDiscount flatDiscount = new FlatRateDiscount();
        flatDiscount.setCode(expectedFlatDiscountCode);
        flatDiscount.setAmount(expectedFlatDiscountAmount);
        flatDiscount.setActive(true);
        flatDiscount.setExpiryDate(Instant.now().plusSeconds(expectedExpirySeconds));
        flatDiscount.setPerAmountSpent(expectedFlatDiscountPerAmount);
        flatDiscount.setMinAccountAgeYears(2);

        when(discountRepository.findAllFlatRateDiscounts()).thenReturn(List.of(flatDiscount));

        // Act
        BillDto result = discountService.applyDiscount(bill);

        // Assert
        assertEquals(expectedTotalPrice, result.getTotalPrice());
        assertEquals(expectedPriceAfterDiscount, result.getPriceAfterDiscount());
        assertTrue(result.getAppliedDiscounts().contains(expectedFlatDiscountCode));
    }

    @Test
    void testApplyDiscount_withOnlyPercentDiscount_appliesPercentDiscount() {
        // Arrange
        final String expectedCategory = "electronics";
        final double expectedItemPrice = 100.0;
        final int expectedItemQuantity = 2;
        final String expectedPercentDiscountCode = "EMP20";
        final double expectedPercentDiscountAmount = 20.0;
        final UserType expectedUserType = UserType.EMPLOYEE;
        final double expectedTotalPrice = 200.0;
        final double expectedPriceAfterDiscount = 160.0; // 20% off 200
        final long expectedExpirySeconds = 3600L;

        ItemDto item = new ItemDto();
        item.setCategory(expectedCategory);
        item.setPrice(expectedItemPrice);
        item.setQuantity(expectedItemQuantity);
        BillDto bill = new BillDto();
        bill.setItems(List.of(item));
        bill.setAppliedDiscounts(new ArrayList<>());

        PercentageBasedDiscount percentDiscount = new PercentageBasedDiscount();
        percentDiscount.setCode(expectedPercentDiscountCode);
        percentDiscount.setAmount(expectedPercentDiscountAmount);
        percentDiscount.setActive(true);
        percentDiscount.setExpiryDate(Instant.now().plusSeconds(expectedExpirySeconds));
        percentDiscount.setUserType(expectedUserType);
        percentDiscount.setExcludedCategories(Collections.emptySet());

        when(discountRepository.findAllPercentageBasedDiscounts()).thenReturn(List.of(percentDiscount));

        // Act
        BillDto result = discountService.applyDiscount(bill);

        // Assert
        assertEquals(expectedTotalPrice, result.getTotalPrice());
        assertEquals(expectedPriceAfterDiscount, result.getPriceAfterDiscount());
        assertTrue(result.getAppliedDiscounts().contains(expectedPercentDiscountCode));
    }

    @Test
    void testApplyDiscount_withPercentDiscountAllItemsExcluded_noDiscountApplied() {
        // Arrange
        final String excludedCategory = "grocery";
        final double expectedItemPrice = 100.0;
        final int expectedItemQuantity = 2;
        final String expectedPercentDiscountCode = "EMP20";
        final double expectedPercentDiscountAmount = 20.0;
        final UserType expectedUserType = UserType.EMPLOYEE;
        final double expectedTotalPrice = 200.0;
        final double expectedPriceAfterDiscount = 200.0; // No discount
        final long expectedExpirySeconds = 3600L;

        ItemDto item = new ItemDto();
        item.setCategory(excludedCategory);
        item.setPrice(expectedItemPrice);
        item.setQuantity(expectedItemQuantity);
        BillDto bill = new BillDto();
        bill.setItems(List.of(item));
        bill.setAppliedDiscounts(new ArrayList<>());

        PercentageBasedDiscount percentDiscount = new PercentageBasedDiscount();
        percentDiscount.setCode(expectedPercentDiscountCode);
        percentDiscount.setAmount(expectedPercentDiscountAmount);
        percentDiscount.setActive(true);
        percentDiscount.setExpiryDate(Instant.now().plusSeconds(expectedExpirySeconds));
        percentDiscount.setUserType(expectedUserType);
        percentDiscount.setExcludedCategories(Set.of(excludedCategory));

        when(discountRepository.findAll()).thenReturn(List.of(percentDiscount));

        // Act
        BillDto result = discountService.applyDiscount(bill);

        // Assert
        assertEquals(expectedTotalPrice, result.getTotalPrice());
        assertEquals(expectedPriceAfterDiscount, result.getPriceAfterDiscount());
        assertTrue(result.getAppliedDiscounts().isEmpty());
    }
}