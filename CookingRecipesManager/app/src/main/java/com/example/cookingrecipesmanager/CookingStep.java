package com.example.cookingrecipesmanager;

public class CookingStep {
    public String stepIntruction;

    //stepType values is only one of [Timer, Basic, Prepare]
    public String stepType;
    //Only "Timer" stepType have timeCounter. Other stepType's timer must set to 0
    public int timerBySecond;

    public CookingStep(String stepIntruction, String stepType, int timerBySecond) {
        this.stepIntruction = stepIntruction;
        this.stepType = stepType;
        if (!stepType.equals("Timer")) {
            timerBySecond = 0;
        } else {
            this.timerBySecond = timerBySecond;
        }
    }
}
