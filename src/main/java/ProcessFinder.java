import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.LogOutputStream;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterators;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
public class ProcessFinder {
    List<Integer> getPids(String username, String processName) {
        List<Integer> pidList = new ArrayList<>();
        try {
            String pids = new ProcessExecutor()
                    .command("/bin/bash", "-c", String.format(
                            /* -opid,args formats ps to return the pid and the args.
                              Swapping the two will return truncated args as shown below, so be careful
                              /usr/lib/gnome-shell/gnome-  1907
                              /usr/lib/gvfs/gvfsd-trash -  2192
                            */
                            "ps -e -opid,args -u%s" +
                                "| grep -v grep " +
                                "| grep %s " +
                                "| awk '{print $1}'", username, processName))
                    //cancel ps if it does not return in 5 seconds
                    .closeTimeout(5, TimeUnit.SECONDS)
                    .exitValueNormal()
                    .redirectError(Slf4jStream.of(getClass()).asInfo())
                    .readOutput(true)
                    .execute()
                    .outputUTF8();


            pidList = Arrays.stream(pids.split("\n"))
                    .map(s -> {
                        try {
                            return Integer.parseInt(s);
                        } catch (NumberFormatException e) {
                            log.error("not a number [{}]",s);
                        }
                        return -1;
                    })
                    .filter(pid -> pid > -1)
                    .collect(Collectors.toList());
        } catch (IOException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
        return pidList;
    }
}
