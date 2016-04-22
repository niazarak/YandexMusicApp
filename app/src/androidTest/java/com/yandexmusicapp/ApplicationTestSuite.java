package com.yandexmusicapp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({CacheTest.class, AdapterTest.class})
public class ApplicationTestSuite {//запускает все тесты
}