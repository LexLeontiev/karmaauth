package com.lexleontiev.karmaauth.ui.main

import android.content.Context
import android.support.v4.app.FragmentManager
import com.lexleontiev.karmaauth.R
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
            0 -> builder.setEndButtonLabel(R.string.next)
            1 -> builder.setEndButtonLabel(R.string.next).setBackButtonLabel(R.string.previous)
            2 -> builder.setEndButtonLabel(R.string.send_to_approve)
                    .setBackButtonLabel(R.string.previous)
            else -> throw IllegalArgumentException("Unsupported position: " + position)
        }
        return builder.create()
    }
}