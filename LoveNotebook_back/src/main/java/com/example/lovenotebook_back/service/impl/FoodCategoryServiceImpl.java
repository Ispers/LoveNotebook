package com.example.lovenotebook_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lovenotebook_back.dao.FoodCategoryDao;
import com.example.lovenotebook_back.entity.FoodCategory;
import com.example.lovenotebook_back.service.FoodCategoryService;
import org.springframework.stereotype.Service;

@Service
public class FoodCategoryServiceImpl extends ServiceImpl<FoodCategoryDao, FoodCategory> implements FoodCategoryService {
}
