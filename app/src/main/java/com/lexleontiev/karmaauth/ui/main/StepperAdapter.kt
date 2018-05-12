package com.lexleontiev.karmaauth.ui.main

import android.content.Context
import android.support.v4.app.FragmentManager
import com.stepstone.stepper.Step
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter
import com.lexleontiev.karmaauth.ui.main.stepper.StepperContract
import com.stepstone.stepper.viewmodel.StepViewModel


class StepperAdapter(fm: FragmentManager, context: Context,
                     private val mStepperListener: StepperContract.Listener)
    : AbstractFragmentStepAdapter(fm,context) {

    override fun getCount(): Int {
        return 3
    }

    override fun createStep(position: Int): Step {
        lateinit var step: Step
        when (position) {
            0 -> step = mStepperListener.createSelectStep()
            1 -> step = mStepperListener.createCorrectStep()
            2 -> step = mStepperListener.createCompleteStep()
        }
        return step
    }


    override fun getViewModel(position: Int): StepViewModel {
        val builder =  StepViewModel.Builder(context)
        when (position) {
            0 -> builder.setEndButtonLabel("Далее")
            1 -> builder.setEndButtonLabel("Далее").setBackButtonLabel("Назад")
            2 -> builder.setEndButtonLabel("Отправить").setBackButtonLabel("Назад")
            else -> throw IllegalArgumentException("Unsupported position: " + position)
        }
        return builder.create()
    }
}