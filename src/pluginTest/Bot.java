package pluginTest;

import org.powertrip.excalibot.common.com.SubTask;
import org.powertrip.excalibot.common.com.SubTaskResult;
import org.powertrip.excalibot.common.plugins.KnightPlug;
import org.powertrip.excalibot.common.plugins.interfaces.knight.ResultManagerInterface;

/**
 * Integração de Sistemas
 * Pedro Filipe Dinis Stamm de Matos, 2009116927
 */
public class Bot extends KnightPlug {
    public Bot(ResultManagerInterface resultManager)  {
        super(resultManager);
    }

    @Override
    public boolean run(SubTask subTask){
        SubTaskResult result = subTask.createResult();
        String msg = subTask.getParameter("message");

        msg += "\nFulfilled by Bot #" + subTask.getKnightInfo().getId();
        result.setSuccessful(true).setResponse("message", msg);
        try {
            resultManager.returnResult(result);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
