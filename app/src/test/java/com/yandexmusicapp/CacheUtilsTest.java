package com.yandexmusicapp;

import android.content.Context;

import com.yandexmusicapp.utils.CacheUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(MockitoJUnitRunner.class)
public class CacheUtilsTest {
    @Mock
    Context app;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void cacheIo_isCorrect() throws IOException {
        CacheUtils.cache = new File("cache.txt");
        CacheUtils.setCache("test");
        assertEquals("test", CacheUtils.getCache());
        assertTrue(CacheUtils.cache.delete());
    }

    @Test
    public void cacheParsing_isCorrect() throws IOException {
        CacheUtils.cache = new File("cache.txt");
        String test1 = "[{\"id\":1080505,\"name\":\"Tove Lo\",\"genres\":[\"pop\",\"dance\",\"electronics\"],\"tracks\":81,\"albums\":22,\"link\":\"http://www.tove-lo.com/\",\"description\":\"шведская певица и автор песен. Она привлекла к себе внимание в 2013 году с выпуском сингла «Habits», но настоящего успеха добилась с ремиксом хип-хоп продюсера Hippie Sabotage на эту песню, который получил название «Stay High». 4 марта 2014 года вышел её дебютный мини-альбом Truth Serum, а 24 сентября этого же года дебютный студийный альбом Queen of the Clouds. Туве Лу является автором песен таких артистов, как Icona Pop, Girls Aloud и Шер Ллойд.\",\"cover\":{\"small\":\"http://avatars.yandex.net/get-music-content/dfc531f5.p.1080505/300x300\",\"big\":\"http://avatars.yandex.net/get-music-content/dfc531f5.p.1080505/1000x1000\"}}]";
        String test = "[{\"id\":1080505,\"name\":\"Tove Lo\",\"genres\":[\"pop\",\"dance\",\"electronics\"],\"tracks\":81,\"albums\":22,\"link\":\"http://www.tove-lo.com/\",\"description\":\"шведская певица и автор песен. Она привлекла к себе внимание в 2013 году с выпуском сингла «Habits», но настоящего успеха добилась с ремиксом хип-хоп продюсера Hippie Sabotage на эту песню, который получил название «Stay High». 4 марта 2014 года вышел её дебютный мини-альбом Truth Serum, а 24 сентября этого же года дебютный студийный альбом Queen of the Clouds. Туве Лу является автором песен таких артистов, как Icona Pop, Girls Aloud и Шер Ллойд.\",\"cover\":{\"small\":\"http://avatars.yandex.net/get-music-content/dfc531f5.p.1080505/300x300\",\"big\":\"http://avatars.yandex.net/get-music-content/dfc531f5.p.1080505/1000x1000\"}}," +
                "{\"id\":2915,\"name\":\"Ne-Yo\",\"genres\":[\"rnb\",\"pop\",\"rap\"],\"tracks\":256,\"albums\":152,\"link\":\"http://www.neyothegentleman.com/\",\"description\":\"обладатель трёх премии Грэмми, американский певец, автор песен, продюсер, актёр, филантроп. В 2009 году журнал Billboard поставил Ни-Йо на 57 место в рейтинге «Артисты десятилетия».\",\"cover\":{\"small\":\"http://avatars.yandex.net/get-music-content/15ae00fc.p.2915/300x300\",\"big\":\"http://avatars.yandex.net/get-music-content/15ae00fc.p.2915/1000x1000\"}}," +
                "{\"id\":91546,\"name\":\"Usher\",\"genres\":[\"rnb\",\"pop\",\"rap\"],\"tracks\":450,\"albums\":183,\"link\":\"http://usherworld.com/\",\"description\":\"американский певец и актёр. Один из самых коммерчески успешных R&B-музыкантов афроамериканского происхождения. В настоящее время продано более 65 миллионов копий его альбомов по всему миру. Выиграл семь премий «Грэмми», четыре премии World Music Awards, четыре премии American Music Award и девятнадцать премий Billboard Music Awards. Владелец собственной звукозаписывающей компании US Records. Он занимает 21 место в списке самых успешных музыкантов по версии Billboard, а также второе место, уступив Эминему в списке самых успешных музыкантов 2000-х годов. В 2010 году журнал Glamour включил его в список 50 самых сексуальных мужчин.\",\"cover\":{\"small\":\"http://avatars.yandex.net/get-music-content/b0e14f75.p.91546/300x300\",\"big\":\"http://avatars.yandex.net/get-music-content/b0e14f75.p.91546/1000x1000\"}}]";
        CacheUtils.setCache(test1);
        assertEquals(1, CacheUtils.getCachedArtists().size());
        CacheUtils.setCache(test);
        assertEquals(3, CacheUtils.getCachedArtists().size());
        assertTrue(CacheUtils.cache.delete());
    }

}