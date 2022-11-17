import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import models.Product;
import repo.ProductRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/user/{id}")
    public List<Product> getAllProductsForUser(@PathVariable long id) {
        return productRepository.getAllProductsForUser(id);
    }

    @GetMapping("/")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Product> getAllProductById(@PathVariable long id) {
        return productRepository.findById(id);
    }

    @GetMapping("/getProductId")
    public long getProductId(@PathVariable Product product) {
        return productRepository.findByDistinct(product.getId(), product.getAmount(), product.getAmountUnit(), product.getCreated_by_id().getId(), product.getDescription(),
                product.getName(), product.getPrice(), product.getPriceUnit());
    }

    @GetMapping("/offers")
    public List<Product> getAllOffers() {
        List<Product> offers = productRepository.findAll();
        offers.removeIf(p -> p.isRequested());
        return offers;
    }

    @GetMapping("/requests")
    public List<Product> getAllRequestedProducts() {
        List<Product> requests = productRepository.findAll();
        requests.removeIf(p -> !p.isRequested());
        return requests;
    }

    @GetMapping("/requests/user/{id}")
    public List<Product> getAllRequestedProductsForUser(@PathVariable long id) {
        List<Product> requests = productRepository.getAllProductsForUser(id);
        requests.removeIf(p -> !p.isRequested());
        return requests;
    }

    @PutMapping("/")
    public Product AddProduct(@RequestBody Product product) {
        System.out.println(product.getCategories());
        System.out.println(product);
        Product presaved = this.productRepository.findById(product.getId()).orElse(null);
        Product savedProduct = productRepository.save(product);
        if (savedProduct.getCategories() == null) {
            return product;
        }
        if (presaved != null) {
            for (int i = 0; i < presaved.getCategories().size(); i++) {
                if (!product.getCategories().contains(presaved.getCategories().get(i))) {
                    productRepository.RemoveCategoryFromProduct(presaved.getCategories().get(i).getId(), savedProduct.getId());
                }
            }
            for (int i = 0; i < product.getCategories().size(); i++) {
                if (!presaved.getCategories().contains(product.getCategories().get(i))) {
                    productRepository.AddCategoryToProduct(product.getCategories().get(i).getId(), savedProduct.getId());
                }
            }
        }
        else {
            for (int i = 0; i < product.getCategories().size(); i++) {
                    productRepository.AddCategoryToProduct(product.getCategories().get(i).getId(), savedProduct.getId());
            }

        }

        return product;
    }

    @PutMapping("/stock")
    public Product updateStock(@RequestBody Product product) {
        productRepository.save(product);
        return product;
    }

    @GetMapping("/requested/{isRequested}")
    public List<Product> getAllProducts(@PathVariable boolean isRequested) {
        return productRepository.findAllByRequest(isRequested);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable long id) {
        productRepository.deleteById(id);
    }

    @GetMapping("/search/")
    public List<Product> getAllForSearch(
            @RequestParam Optional<String> searchQuery) {
        System.out.println(searchQuery.orElse("_"));
        return productRepository.findProductByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContaining(
                searchQuery.orElse("_"),
                searchQuery.orElse("_"));
    }
}
