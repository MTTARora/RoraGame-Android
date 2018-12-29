package com.rorasoft.roragame.computers;

import com.roragame.nvstream.http.ComputerDetails;

public interface ComputerManagerListener {
    void notifyComputerUpdated(ComputerDetails details);
}
