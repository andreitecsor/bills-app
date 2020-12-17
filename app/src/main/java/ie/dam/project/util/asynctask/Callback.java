package ie.dam.project.util.asynctask;

public interface Callback<R> {
    void runResultOnUiThread(R result);
}
