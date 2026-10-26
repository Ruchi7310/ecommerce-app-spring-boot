package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartService cartService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException("category", "categoryId", categoryId));

        boolean isProductNotPresent = true;

        List<Product> products = category.getProducts();

        for (Product value : products) {
            if (value.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;

            }

        }
        if (isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() -
                    (product.getDiscount() * 0.01) * product.getPrice();
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);



        }else{
            throw new APIException("Product already exists");
    }
}




    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber, pageSize,sortByAndOrder);
        Page<Product> pageProduct= productRepository.findAll(pageDetails);

       List<Product> products= pageProduct.getContent();
       List<ProductDTO> productDTO =    products.stream().map(product->modelMapper.map(product,ProductDTO.class))
               .toList();

//       if(products.isEmpty()){
//           throw new APIException("product not found");
//       }


       ProductResponse productResponse = new ProductResponse();
       productResponse.setConent(productDTO);
       productResponse.setPageNumber(pageProduct.getNumber());
       productResponse.setPageSize(pageProduct.getSize());
       productResponse.setTotalPages(pageProduct.getTotalPages());
       productResponse.setTotalElements(pageProduct.getTotalElements());
       productResponse.setLastPage(pageProduct.isLast());

        return  productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

     Category  category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException("category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber, pageSize,sortByAndOrder);
        Page<Product> pageProduct= (Page<Product>) productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);

        List<Product> products= pageProduct.getContent();

        if(products.isEmpty()){
            throw new APIException("product not found with category"+category.getCategoryName());

        }

        List<ProductDTO> productDTO = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setConent(productDTO);
        productResponse.setPageNumber(pageProduct.getNumber());
        productResponse.setPageSize(pageProduct.getSize());
        productResponse.setTotalPages(pageProduct.getTotalPages());
        productResponse.setTotalElements(pageProduct.getTotalElements());
        productResponse.setLastPage(pageProduct.isLast());
        return productResponse;

    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber, pageSize,sortByAndOrder);
       Page<Product>pageProduct=productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%',pageDetails);

         List<Product> products= pageProduct.getContent();
        List<ProductDTO> productDTO =    products.stream().map(product->modelMapper.map(product,ProductDTO.class))
                .toList();

        if(products.isEmpty()){
            throw new APIException("product not found with keyword"+keyword);

}
        ProductResponse productResponse = new ProductResponse();
        productResponse.setConent(productDTO);
        productResponse.setPageNumber(pageProduct.getNumber());
        productResponse.setPageSize(pageProduct.getSize());
        productResponse.setTotalPages(pageProduct.getTotalPages());
        productResponse.setTotalElements(pageProduct.getTotalElements());
        productResponse.setLastPage(pageProduct.isLast());


        return  productResponse;


    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
//        get the existing product from the database;
      Product productFromDb=  productRepository.findById(productId)
              .orElseThrow(()->new  ResourceNotFoundException("product", "productId", productId));

      Product product=modelMapper.map(productDTO,Product.class);
//        update product info
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setSpecialPrice(product.getSpecialPrice());
//        save the product in to the database
      Product savedProduct=  productRepository.save(productFromDb);

      List<Cart>carts=cartRepository.findCartsByProductId(productId);
   List<CartDTO> cartDTOs =carts.stream().map(cart -> {
       CartDTO cartDTO= modelMapper.map(cart, CartDTO.class);
       List<ProductDTO>products=cart.getCartItems().stream().map(p->modelMapper.map(p.getProduct(),ProductDTO.class)).collect(Collectors.toList());
       cartDTO.setProducts(products);
       return cartDTO;
   }).toList();
   cartDTOs.forEach(cart->cartService.updateProductInCart(cart.getCartId(),productId));
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product=  productRepository.findById(productId)
                .orElseThrow(()->new  ResourceNotFoundException("product", "productId", productId));
        List<Cart>carts=cartRepository.findCartsByProductId(productId);
        carts.forEach(cart->cartService.deleteProductFromCart(cart.getCartId(),productId));

     productRepository.delete(product);
        return modelMapper.map(product,ProductDTO.class);

    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
//        get product from db
        Product productFromDb=  productRepository.findById(productId)
                .orElseThrow(()->new  ResourceNotFoundException("product", "productId", productId));
//        upload image to server
//        get file name of uploaded image
        String fileName = fileService.uploadImage(path,image);
//        updating new file name to the product
        productFromDb.setImage(fileName);
//        save the product
        Product updatedProduct=  productRepository.save(productFromDb);
//        return DTO after mapping pproduct to dto
        return modelMapper.map(updatedProduct,ProductDTO.class);

    }




}
