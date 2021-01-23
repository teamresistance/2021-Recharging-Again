package frc.robot.auto.holding;

public class AutoSelector {

    //TODO: change name
    private static Auto path = new Auto();

    public static void select(int selection) {
        switch (selection) {
            case 1:
                path = new Auto(/*trajectory object*/);
                break;
            case 2:
                path = new Auto(/*trajectory object */);
                break;
        }
    }

    public static void init() {
        path.init();
    }

    public static void execute() {
        path.execute();
    }

    public static boolean done() {
        return path.done();
    }

}
