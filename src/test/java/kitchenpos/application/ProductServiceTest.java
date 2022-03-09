package kitchenpos.application;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @DisplayName("상품에는 반드시 이름이 있어야 한다.")
    @NullSource
    @ParameterizedTest
    void nameIsMandatory(String name) {
        assertThatThrownBy(() -> createProduct(name, new BigDecimal("15000")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 이름에는 비속어를 사용할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"xxxx"})
    void nameContainsProfanity(String name) {
        assertThatThrownBy(() -> createProduct(name, new BigDecimal("15000")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품에는 반드시 가격이 있어야 한다.")
    @Test
    void priceIsMandatory() {
        assertThatThrownBy(() -> createProduct("후라이드치킨", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격은 0보다 작을 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "-1000"})
    void negativePrice(String price) {
        assertThatThrownBy(() -> createProduct("후라이드치킨", new BigDecimal(price)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        // given
        Product product = createProduct("후라이드치킨", BigDecimal.ZERO);

        // when
        Product priceChangeRequest = new Product();
        priceChangeRequest.setPrice(new BigDecimal("1000"));
        productService.changePrice(product.getId(), priceChangeRequest);

        // then
        Optional<Product> changedProduct = productRepository.findById(product.getId());
        assertThat(changedProduct).isPresent();
        assertThat(changedProduct.get().getPrice()).isEqualByComparingTo(new BigDecimal("1000"));
    }

    @DisplayName("등록한 상품 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        Product product1 = createProduct("후라이드치킨", new BigDecimal("15000"));
        Product product2 = createProduct("양념치킨", new BigDecimal("16000"));

        // when
        List<Product> products = productService.findAll();

        // then
        assertThat(products.stream().map(Product::getId).toArray()).contains(new UUID[] {product1.getId(), product2.getId()});
    }

    public Product createProduct(String name, BigDecimal price) {
        Product productRequest = new Product();
        productRequest.setName(name);
        productRequest.setPrice(price);
        return productService.create(productRequest);
    }
}
