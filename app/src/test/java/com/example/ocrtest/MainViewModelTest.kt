package com.example.ocrtest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
@RunWith(JUnit4::class)
class MainViewModelTest {

lateinit var viewModel: MainViewModel
    @Mock
    lateinit var repo: MainRepo
    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
//        viewModel = mock(MainViewModel::class.java)
        viewModel = MainViewModel(repo)
    }
    @Test
    fun getPatternString() {
        val inputStr ="MW123456"
        val outStr: String = viewModel.getPatternString(inputStr)
        assertEquals(
            "Pattern result",
            inputStr, outStr
        )
    }

    @Test
    fun isValidSeries() {
        val inputStr ="MW123456"
        val outStr = viewModel.isValidSeries(inputStr)
        assertEquals(
            "Pattern result",
            true, outStr
        )
    }
}