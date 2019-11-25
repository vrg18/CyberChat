package edu.vrg18.cyber_chat.util;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PaginationAssistant {

    public static void assistant (String entityName, Model model, Page page) {

        model.addAttribute(entityName.concat("s"), page.getContent());
        int totalPages = page.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute(entityName.concat("PageNumbers"), pageNumbers);
            model.addAttribute(entityName.concat("TotalPages"), page.getTotalPages());
            model.addAttribute(entityName.concat("PageSize"), page.getSize());
            model.addAttribute(entityName.concat("CurrentPage"), page.getNumber() + 1);
        }
    }
}


