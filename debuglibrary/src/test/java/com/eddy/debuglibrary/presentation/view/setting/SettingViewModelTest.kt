package com.eddy.debuglibrary.presentation.view.setting

import com.eddy.debuglibrary.presentation.view.ui.setting.SettingContract
import com.eddy.debuglibrary.presentation.view.ui.setting.viewmodel.SettingViewModel
import com.eddy.debuglibrary.util.di.MainCoroutineRule
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var settingViewModel: SettingViewModel

    @Before
    fun setupViewModel() {
        settingViewModel = SettingViewModel()
    }

    @Test
    fun `viewModel의 상태를 init 상태로 만들 때`() {
        // Given
        val viewModel = SettingViewModel()

        // When
        val initialState = viewModel.createInitialState()

        // Then
        assertEquals(initialState, SettingContract.State(SettingContract.SettingState.Init))
    }

    @Test
    fun `딜리트 버튼을 클릭 하면 viewModel 은 delete SideEffect 를 가진다 `()= runTest {
        // Given
        val viewModel = SettingViewModel()
        val keyword = "test keyword"

        // When
        viewModel.onClickDeleteKeyword(keyword)

        // Then
        assertEquals(SettingContract.SideEffect.DeleteKeyword(keyword), viewModel.effect.first())
    }

    @Test
    fun `배경 진하게 버튼을 활성화 하면 `
}