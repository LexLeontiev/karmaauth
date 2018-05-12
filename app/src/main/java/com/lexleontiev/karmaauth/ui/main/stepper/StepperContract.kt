package com.lexleontiev.karmaauth.ui.main.stepper

import com.lexleontiev.karmaauth.ui.main.step.completedoc.CompleteDocContract
import com.lexleontiev.karmaauth.ui.main.step.correctdoc.CorrectDocContract
import com.lexleontiev.karmaauth.ui.main.step.selectdoc.SelectDocContract


interface StepperContract {

    interface Listener {

        fun createSelectStep(): SelectDocContract.View

        fun createCorrectStep(): CorrectDocContract.View

        fun createCompleteStep(): CompleteDocContract.View
    }
}