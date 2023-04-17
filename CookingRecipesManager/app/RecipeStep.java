public class RecipeStep {
    public String stepName;
    public String stepType;
    public String stepDetail;
    public int stepTimer;

    public RecipeStep(String stepName, String stepType, String stepDetail, int stepTimer) {
        this.stepName = stepName;
        this.stepType = stepType;
        this.stepDetail = stepDetail;
        this.stepTimer = stepTimer;
    }
}
