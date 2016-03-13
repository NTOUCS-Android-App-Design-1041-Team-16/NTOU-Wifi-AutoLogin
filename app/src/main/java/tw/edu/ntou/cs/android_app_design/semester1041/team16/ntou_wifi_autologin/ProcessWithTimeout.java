package tw.edu.ntou.cs.android_app_design.semester1041.team16.ntou_wifi_autologin;

public class ProcessWithTimeout extends Thread {
    private Process mProcess;
    private int mExitCode = Integer.MIN_VALUE;

    public ProcessWithTimeout(Process p_process) {
        mProcess = p_process;
    }

    public int waitForProcess(int pTimeoutMilliseconds) {
        this.start();

        try {
            this.join(pTimeoutMilliseconds);
        }
        catch (InterruptedException e) {
            this.interrupt();
        }

        return mExitCode;
    }

    @Override
    public void run() {
        try {
            mExitCode = mProcess.waitFor();
        }
        catch (InterruptedException ignore) {
            // Do nothing
        }
        catch (Exception ex) {
            // Unexpected exception
        }
    }
}