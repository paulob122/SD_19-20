
package app.server.tools;

import app.utils.Config;
import app.utils.GeneralMessage;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implements a buffer of download requests in order to set up a MAX_DOWN downloads
 * for the server requests. It also manages waiting clients by its download size.
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class DownloadRequestsBuffer {

    //------------------------------------------------------------------------------------------------------------------

    /**
     * ClientIdentifier queue for the users downloading at the moment.
     */
    private ClientIdentifier[] downloadQueue;
    /**
     * Next position in the download queue
     */
    private int nextPosDownloadQueue;
    /**
     * Maximum queue size. This value is established by the config value MAX_DOWN.
     */
    private int downloadQueue_MAX_SIZE;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Monitor for the waiting clients trying to get download request done
     */
    private ReentrantLock lock;
    /**
     * Waiting condition for the small downloads
     */
    private Condition smallDownloadCondition;
    /**
     * Waiting condition for the big downloads
     */
    private Condition bigDownloadCondition;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Number of small downloads that got the lock.
     */
    private int small_downloads_atm;
    /**
     * Number of big downloads that got the lock.
     */
    private int big_downloads_atm;

    /**
     * Number of small downloads requests.
     */
    private int small_downloads_requests;
    /**
     * Number of big download requests.
     */
    private int big_downloads_requests;

    /**
     * Small downloads priority
     */
    private int small_downloads_Priority;
    /**
     * big downloads priority
     */
    private int big_downloads_Priority;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * server config based on config.cnf
     */
    private Config config;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Parametrized constructor for DownloadRequestsBuffer
     * @param config server config
     */
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

    /**
     * @return a representation of the download queue as a string
     */
    public String toString() {

        StringBuilder sb = new StringBuilder();

        int r = 0;

        for (ClientIdentifier c: this.downloadQueue) {

            sb.append("pos " + r + ": " + c.toString() + ";\n");
        }

        return sb.toString();
    }

    /**
     * Attempts to get to download buffer. Gets stuck in waiting condition in case
     * it doesnt get any preiority.
     *
     * @param c_id client id
     */
    public synchronized void push(ClientIdentifier c_id) {

        int r = 0;

        try {

            GeneralMessage.show(3, c_id.getUserName(), "waiting to download " + c_id.getDownloadSize(), true);

            while (this.nextPosDownloadQueue == this.downloadQueue_MAX_SIZE) {

                GeneralMessage.show(3, c_id.getUserName(), "queue is full, waiting... " + c_id.getDownloadSize(), true);

                wait();

                r = 1;
            }

        } catch (InterruptedException e) {

            e.printStackTrace();
        }
        if (r == 1) {
            if (isBigDowload(c_id.getDownloadSize())) {
                try {
                    lockBigDownload();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                unlockBigDownload();
            }
            else {
                try {
                    lockSmallDownload();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                unlockSmallDownload();
            }
        }

        GeneralMessage.show(3, c_id.getUserName(), "download accepted " + c_id.getDownloadSize(), true);

        this.downloadQueue[this.nextPosDownloadQueue] = c_id;
        this.nextPosDownloadQueue++;

        notifyAll();
    }

    /**
     * Trys to get priority in small download
     * @throws InterruptedException
     */
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

    /**
     * Unlocks priority in small download
     * @throws InterruptedException
     */
    public void unlockSmallDownload() {

        this.lock.lock();

        this.small_downloads_atm--;

        if (this.small_downloads_atm == 0) {

            this.bigDownloadCondition.signal();
        }

        this.lock.unlock();
    }

    /**
     * Trys to get priority as a big download
     * @throws InterruptedException
     */
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

    /**
     * Unlocks priority in big download
     */
    public void unlockBigDownload() {

        this.lock.lock();

        this.big_downloads_atm = 0;

        this.smallDownloadCondition.signalAll();
        this.bigDownloadCondition.signal();

        this.lock.unlock();
    }

    /**
     * Removes user from the user downloading queue and notifies waiting threads (clients).
     * @throws InterruptedException
     */
    public synchronized void pop() throws InterruptedException {

        while (this.nextPosDownloadQueue == 0) {

            wait();
        }

        this.nextPosDownloadQueue--;

        notifyAll();
    }

    /**
     * Says if the download size matches a big download
     * @param size download size
     * @return true in case its a big download
     */
    public boolean isBigDowload(double size) {

        return size > 51229;
    }
}
