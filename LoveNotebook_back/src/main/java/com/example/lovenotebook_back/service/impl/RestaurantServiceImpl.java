package com.example.lovenotebook_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lovenotebook_back.dao.RestaurantDao;
import com.example.lovenotebook_back.entity.Restaurant;
import com.example.lovenotebook_back.service.RestaurantService;
import org.springframework.stereotype.Service;

@Service
public class RestaurantServiceImpl extends ServiceImpl<RestaurantDao, Restaurant> implements RestaurantService {
}
