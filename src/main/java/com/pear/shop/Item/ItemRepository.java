package com.pear.shop.Item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findPageBy(Pageable page);
    List<Item> findAllByTitleContains(String title);

//    @Query(value = "select * from item where match(title) against(?1)", nativeQuery = true)
//    List<Item> rawQuery1(String text);

    @Query(value = "SELECT * FROM item WHERE MATCH(title) AGAINST(?1) OR price LIKE CONCAT('%',?1,'%')",
    countQuery = "SELECT COUNT(*) FROM item WHERE MATCH(title) AGAINST(?1) OR price LIKE CONCAT('%',?1,'%')",
    nativeQuery = true)
    Page<Item> searchByTitleOrPrice(String text, Pageable pageable);
}
