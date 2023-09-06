package com.example.lovenotebook_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lovenotebook_back.dao.FoodDao;
import com.example.lovenotebook_back.entity.Food;
import com.example.lovenotebook_back.service.FoodService;
import org.springframework.stereotype.Service;

@Service
public class FoodServiceImpl extends ServiceImpl<FoodDao, Food> implements FoodService {
}
