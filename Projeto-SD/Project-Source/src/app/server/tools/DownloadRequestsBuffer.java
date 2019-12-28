
package app.server.tools;

import app.utils.Config;
import app.utils.GeneralMessage;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DownloadRequestsBuffer {

    //------------------------------------------------------------------------------------------------------------------

    //Setting up the bounded buffer to store downloads atm

    private ClientIdentifier[] downloadQueue;
    private int nextPosDownloadQueue;
    private int downloadQueue_MAX_SIZE;

    //------------------------------------------------------------------------------------------------------------------

    private ReentrantLock lock;

    private Condition smallDownloadCondition;
    private Condition bigDownloadCondition;

    //------------------------------------------------------------------------------------------------------------------

    private int small_downloads_atm;
    private int big_downloads_atm;

    private int small_downloads_requests;
    private int big_downloads_requests;

    private int small_downloads_Priority;
    private int big_downloads_Priority;

    //------------------------------------------------------------------------------------------------------------------

    private Config config;

    //------------------------------------------------------------------------------------------------------------------

    public DownloadRequestsBuffer(Config config) {

        this.config = config;

        //Setting up the download queue
        this.downloadQueue_MAX_SIZE = config.getMAX_DOWN();
        this.downloadQueue = new ClientIdentifier[this.downloadQueue_MAX_SIZE];
        this.nextPosDownloadQueue = 0;

        this.lock = new ReentrantLock();

        this.smallDownloadCondition = lock.newCondition();
        this.bigDownloadCondition = lock.newCondition();

        this.small_downloads_requests = this.big_downloads_requests = 0;
        this.big_downloads_atm = this.small_downloads_atm = 0;
        this.big_downloads_Priority = this.small_downloads_Priority = 0;

    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        int r = 0;

        for (ClientIdentifier c: this.downloadQueue) {

            sb.append("pos " + r + ": " + c.toString() + ";\n");
        }

        return sb.toString();
    }

    public synchronized void push(ClientIdentifier c_id) {

        int r = 0;

        try {

            GeneralMessage.show(3, c_id.getUserName(), "waiting to download " + c_id.getDownloadSize(), true);

            while (this.nextPosDownloadQueue == this.downloadQueue_MAX_SIZE) {

                if (r == 1) {

                    if (isBigDowload(c_id.getDownloadSize())){
                        unlockBigDownload();}
                    else {unlockSmallDownload();}
                }

                GeneralMessage.show(3, c_id.getUserName(), "queue is full, waiting... " + c_id.getDownloadSize(), true);

                wait();

                if (isBigDowload(c_id.getDownloadSize())){
                    lockBigDownload();}
                else {lockSmallDownload();}

                r = 1;
            }

        } catch (InterruptedException e) {

            e.printStackTrace();
        }

        if (r == 1) {
            if (isBigDowload(c_id.getDownloadSize())) {
                unlockBigDownload();
            }
            else {
                unlockSmallDownload();
            }
        }

        GeneralMessage.show(3, c_id.getUserName(), "download accepted " + c_id.getDownloadSize(), true);

        this.downloadQueue[this.nextPosDownloadQueue] = c_id;
        this.nextPosDownloadQueue++;

        notifyAll();
    }

    private void lockSmallDownload() throws InterruptedException {

        this.lock.lock();

        this.small_downloads_requests++;

        while (this.big_downloads_atm == 1 ||
                (this.big_downloads_requests > 0 &&
                        this.small_downloads_Priority >= 3)) {

            this.smallDownloadCondition.await();
        }

        this.small_downloads_requests--;
        this.small_downloads_atm++;

        this.big_downloads_Priority = 0;
        this.small_downloads_Priority++;

        this.lock.unlock();
    }

    public void unlockSmallDownload() {

        this.lock.lock();

        this.small_downloads_atm--;

        if (this.small_downloads_atm == 0) {

            this.bigDownloadCondition.signal();
        }

        this.lock.unlock();
    }

    private void lockBigDownload() throws InterruptedException {

        this.lock.lock();

        this.big_downloads_requests++;

        while (this.small_downloads_atm > 0
                || this.big_downloads_atm > 0
                || (this.small_downloads_requests > 0
                && this.big_downloads_Priority >= 3)) {

            this.bigDownloadCondition.await();
        }

        this.big_downloads_requests--;
        this.big_downloads_atm = 1;

        this.small_downloads_Priority = 0;
        this.big_downloads_Priority ++;

        this.lock.unlock();
    }

    public void unlockBigDownload() {

        this.lock.lock();

        this.big_downloads_atm = 0;

        this.smallDownloadCondition.signalAll();
        this.bigDownloadCondition.signal();

        this.lock.unlock();
    }

    public synchronized void pop() throws InterruptedException {

        while (this.nextPosDownloadQueue == 0) {

            wait();
        }

        this.nextPosDownloadQueue--;

        notifyAll();
    }

    public boolean isBigDowload(double size) {

        return size > 51229;
    }
}
