package pluginTest;

import org.powertrip.excalibot.common.com.*;
import org.powertrip.excalibot.common.plugins.ArthurPlug;
import org.powertrip.excalibot.common.plugins.interfaces.arthur.KnightManagerInterface;
import org.powertrip.excalibot.common.plugins.interfaces.arthur.TaskManagerInterface;
import org.powertrip.excalibot.common.utils.logging.Logger;

import java.util.List;
import java.util.Map;

/**
 * Integração de Sistemas
 * Pedro Filipe Dinis Stamm de Matos, 2009116927
 */
public class Server extends ArthurPlug {
    public Server(KnightManagerInterface knightManager, TaskManagerInterface taskManager) {
        super(knightManager, taskManager);
    }

    @Override
    public PluginHelp help() {
        return new PluginHelp().setHelp(
                "::Test Plugin " +
                "Usage: pluginTest message:<message> bots:<bots>"
        );
    }

    @Override
    public TaskResult check(Task task) {
        TaskResult result = new TaskResult();

        Long total = taskManager.getKnightCount(task.getTaskId());
        Long recev = taskManager.getResultCount(task.getTaskId());

        result
                .setSuccessful(true)
                .setTaskId(task.getTaskId())
                .setResponse("total", total.toString())
                .setResponse("done", recev.toString())
                .setComplete(total.equals(recev));
        return result;
    }

    @Override
    public TaskResult get(Task task) {
        Long total = taskManager.getKnightCount(task.getTaskId());
        Long recev = taskManager.getResultCount(task.getTaskId());

        TaskResult result = new TaskResult()
                .setTaskId(task.getTaskId())
                .setSuccessful(true)
                .setComplete(total.equals(recev));

        List<SubTaskResult> resultsList = taskManager.getAllResults(task.getTaskId());
        if(resultsList.isEmpty()){
            result.setResponse("stdout", "No results received.");
        }
        String resultOutput = "Messages received:";
        for(SubTaskResult res : resultsList) {
            result.setResponse("Knight"+res.getKnightId().toString(), res.getResponse("message"));
        }
        return result;
    }

    @Override
    public void handleSubTaskResult(Task task, SubTaskResult subTaskResult) {
        Logger.log("Bot "+subTaskResult.getKnightId()+" responded.");
    }

    @Override
    public TaskResult submit(Task task) {
        Logger.log(task.toString());
        Map args = task.getParametersMap();

        String msg;
        long botCount;

        TaskResult result = new TaskResult()
                .setTaskId(task.getTaskId())
                .setSuccessful(false)
                .setComplete(true);

        if(!args.containsKey("message") || !args.containsKey("bots"))
            return result.setResponse("stdout", "Wrong parameters");

        msg = (String) args.get("message");

        List<KnightInfo> bots = knightManager.getFreeKnightList(30000);
        for(KnightInfo bot : bots){
            knightManager.dispatchToKnight(
                    new SubTask(task, bot)
                        .setParameter("message", msg)
            );
        }
        result.setSuccessful(true)
                .setResponse("stdout", "Task accepted. Message \""+msg+"\"will be returned shortly.");
        return result;
    }
}
