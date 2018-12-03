package com.rorasoft.roragame.Model.computers;

import com.roragame.nvstream.http.ComputerDetails;

public interface ComputerManagerListener {
    void notifyComputerUpdated(ComputerDetails details);
}
