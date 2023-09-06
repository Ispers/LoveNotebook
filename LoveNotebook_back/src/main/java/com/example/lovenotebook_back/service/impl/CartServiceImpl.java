package com.example.lovenotebook_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lovenotebook_back.dao.CartDao;
import com.example.lovenotebook_back.entity.Cart;
import com.example.lovenotebook_back.service.CartService;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl extends ServiceImpl<CartDao, Cart> implements CartService {
}
