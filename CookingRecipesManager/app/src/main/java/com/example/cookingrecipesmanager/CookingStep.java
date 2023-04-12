package com.example.cookingrecipesmanager;

public class CookingStep {
    public String stepName;
    public String stepDetailInstruction;

    //stepType values is only one of [Timer, Basic, Prepare]
    public String stepType;
    //Only "Timer" stepType have timeCounter. Other stepType's timer must set to 0
    public int timerBySecond;

    public CookingStep(String stepName, String stepDetailInstruction, String stepType, int timerBySecond) {
        this.stepName = stepName;
        this.stepDetailInstruction = stepDetailInstruction;
        this.stepType = stepType;
        if (stepType.equals("Timer")) {
            timerBySecond = 0;
        } else {
            this.timerBySecond = timerBySecond;
        }
    }
}
