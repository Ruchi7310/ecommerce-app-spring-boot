package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {
//    List<Category> categories = new ArrayList<Category>();

    @Autowired
 private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
       Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
               ?Sort.by(sortBy).ascending()
               : Sort.by(sortBy).descending();


        Pageable pageDetails = PageRequest.of(pageNumber, pageSize,sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories =categoryPage.getContent();
        if(categories.isEmpty()){
            throw new APIException("Category Not Found");
        }
        List<CategoryDTO>categoryDTO=categories.stream().map(category->modelMapper.map(category,CategoryDTO.class))
                .toList();
        CategoryResponse categoryResponse=new CategoryResponse();
        categoryResponse.setContent(categoryDTO);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setLastPage(categoryPage.isLast());
        return  categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category=modelMapper.map(categoryDTO,Category.class);
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if(savedCategory!=null){
            throw new APIException("Category already exists with name"+ category.getCategoryName());
        }
      Category saveCategory=  categoryRepository.save(category);
        return modelMapper.map(saveCategory,CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategoryId(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

//        List<Category> categories = categoryRepository.findAll();
//        Category category = categories.stream().filter(c -> c.getCategoryId() == categoryId).findFirst().
//                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
////        if(category==null)
//            return "Category not fuond";

        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }
    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {

        Category savedCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));
        Category category=modelMapper.map(categoryDTO,Category.class);
        category.setCategoryId(categoryId);

        savedCategory=categoryRepository.save(category);
        return  modelMapper.map(savedCategory, CategoryDTO.class);

//        Optional<Category> optionalCategory = categories.stream().filter(c -> c.getCategoryId() == categoryId).findFirst();
//        if (optionalCategory.isPresent()) {
//            Category category1 = optionalCategory.get();
//            category1.setCategoryName(category.getCategoryName());
//            Category savedCategory = categoryRepository.save(category1);
//            return savedCategory;
//        }else{
//            throw new ResourceNotFoundException("Category","CategoryId",categoryId);
//        }


    }


    }
