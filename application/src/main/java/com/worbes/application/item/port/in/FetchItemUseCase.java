package com.worbes.application.item.port.in;

import com.worbes.application.item.model.Item;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface FetchItemUseCase {

    List<Item> fetchItemAsync(Set<Long> itemIds) throws InterruptedException, ExecutionException, TimeoutException;
}
