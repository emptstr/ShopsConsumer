package shpe.consumer.app;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shpe.consumer.controller.ActiveEventUpdateController;
import shpe.consumer.controller.TokenSetController;
import shpe.consumer.model.TokenSet;

public class ConsumerRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class);
    private final TokenSetController tokenSetController;
    private final ActiveEventUpdateController updateManager;

    @Inject
    public ConsumerRunnable(final TokenSetController tokenSetController, final ActiveEventUpdateController updateManager) {
        this.tokenSetController = tokenSetController;
        this.updateManager = updateManager;
    }

    public void run() {
        try {
            logger.info("Started api tool runner");
            TokenSet apiTokens = tokenSetController.retrieveTokenSet();
            updateManager.update(apiTokens.getAccessToken());
            logger.info("Api Tool Runner finished");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}