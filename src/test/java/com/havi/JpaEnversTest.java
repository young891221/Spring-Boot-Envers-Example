package com.havi;

import com.havi.domain.Book;
import com.havi.repository.BookRepository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by KimYJ on 2017-08-29.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaEnversTest {
    private static boolean isInit = false;

    @Autowired
    private BookRepository bookRepository;

    @Before
    public void Book_데이터_생성() {
        if(!isInit) {
            //10개 Book 데이터 저장
            IntStream.rangeClosed(1, 10).forEach(index ->
                    bookRepository.save(Book.builder().title("테스트" + index).publishedAt(Timestamp.valueOf(LocalDateTime.now())).build())
            );

            //1번 Book 삭제
            bookRepository.delete(Long.valueOf(1));

            //2번 Book 수정하기 3번 반복
            Book book2 = bookRepository.findOne(Long.valueOf(2));
            IntStream.rangeClosed(1, 3).forEach(index -> {
                book2.setTitle("수정" + index);
                bookRepository.save(book2);
            });
            isInit = true;
        }
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void Book_Revision_NULL_데이터_검색() {
        Revision revision = bookRepository.findLastChangeRevision(Long.valueOf(1));
        Book book = (Book) revision.getEntity();
        Integer revisionNumber = (Integer) revision.getRevisionNumber();

        assertThat(book.getTitle(), is("테스트1"));
        assertThat(revisionNumber, is(1));
    }

    @Test
    public void Book_Revision_검색() {
        Revision revision1 = bookRepository.findRevision(Long.valueOf(1),1);
        Book book1 = (Book) revision1.getEntity();
        Integer revisionNumber1 = (Integer) revision1.getRevisionNumber();

        assertThat(book1.getTitle(), is("테스트1"));
        assertThat(revisionNumber1, is(1));

        Revision revision2 = bookRepository.findLastChangeRevision(Long.valueOf(2));
        Book book2 = (Book) revision2.getEntity();
        Integer revisionNumber2 = (Integer) revision2.getRevisionNumber();
        DateTime dateTime = revision2.getRevisionDate();

        assertThat(book2.getTitle(), is("수정3"));
        assertThat(revisionNumber2, is(14));
    }

    @Test
    public void Book_Revision_Page_검색() {
        Page<Revision<Integer, Book>> bookPage = bookRepository.findRevisions(Long.valueOf(2), new PageRequest(0, 10));
        assertThat(bookPage.getTotalElements(), is(Long.valueOf(4)));
        assertThat(bookPage.getContent().get(0).getEntity().getTitle(), is("테스트2"));
        assertThat(bookPage.getContent().get(1).getEntity().getTitle(), is("수정1"));
        assertThat(bookPage.getContent().get(2).getEntity().getTitle(), is("수정2"));
        assertThat(bookPage.getContent().get(3).getEntity().getTitle(), is("수정3"));
    }

    @Test
    public void Book_Revisions_검색() {
        Revisions<Integer, Book> revisions = bookRepository.findRevisions(Long.valueOf(2));
        assertThat(revisions.getContent().size(), is(4));
        assertThat(revisions.getContent().get(0).getEntity().getTitle(), is("테스트2"));
        assertThat(revisions.getContent().get(1).getEntity().getTitle(), is("수정1"));
        assertThat(revisions.getContent().get(2).getEntity().getTitle(), is("수정2"));
        assertThat(revisions.getContent().get(3).getEntity().getTitle(), is("수정3"));
    }
}
