package com.havi;

import com.havi.domain.Book;
import com.havi.repository.BookRepository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.history.Revision;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

/**
 * Created by KimYJ on 2017-08-29.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaEnversTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void Book_데이터_생성() {
        IntStream.rangeClosed(1, 10).forEach(index ->
            bookRepository.save(Book.builder().title("테스트"+index).publishedAt(Timestamp.valueOf(LocalDateTime.now())).build())
        );
    }

    @Test
    public void Book_Revision_검색() {
        Revision revision = bookRepository.findLastChangeRevision(Long.valueOf(1));

        Book book = (Book) revision.getEntity();
        Integer revisionNumber = (Integer) revision.getRevisionNumber();
        DateTime dateTime = revision.getRevisionDate();

        System.out.println(revision);
        System.out.println(book);
        System.out.println(revisionNumber);
        System.out.println(dateTime);
    }

}
