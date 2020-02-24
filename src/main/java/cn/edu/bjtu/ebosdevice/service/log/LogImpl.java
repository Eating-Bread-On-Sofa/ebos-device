package cn.edu.bjtu.ebosdevice.service.log;

import cn.edu.bjtu.ebosdevice.service.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogImpl implements Log {
    @Autowired
    private static final Logger logger = LogManager.getLogger("cn.edu.bjtu.ebosdevice.service.log");
    @Override
    public void debug(String message) {
        logger.debug(message);
    }
    @Override
    public void debug(String message, Exception e) {
        logger.debug(message, e);
    }
    @Override
    public void info(String message) {
        logger.info(message);
    }
    @Override
    public void info(String message, Exception e) {
        logger.info(message, e);
    }
    @Override
    public void warn(String message) {
        logger.warn(message);
    }
    @Override
    public void warn(String message, Exception e) {
        logger.warn(message, e);
    }
    @Override
    public void error(String message) {
        logger.error(message);
    }
    @Override
    public void error(String message, Exception e) {
        logger.error(message, e);
    }
}
