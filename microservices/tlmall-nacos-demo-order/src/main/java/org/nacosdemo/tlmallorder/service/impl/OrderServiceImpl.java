/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nacosdemo.tlmallorder.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.nacosdemo.tlmallorder.entity.Order;
import org.nacosdemo.tlmallorder.mapper.OrderMapper;
import org.nacosdemo.tlmallorder.service.OrderService;
import org.springcloudmvp.tlmallcommon.Result;

import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;


    @Override
    public Result<?> getOrderByUserId(String userId) {
        List<Order> list = orderMapper.getOrderByUserId(userId);

        return Result.success(list);
    }

    public Result<?> getOrderById(Integer id) {

        Order order = orderMapper.getOrderById(id);
        return Result.success(order);
    }

}
