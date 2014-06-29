package ctdownloader.util.test;

import java.util.ArrayList;

import org.eclipse.mylyn.context.core.IInteractionContext;
import org.junit.Before;
import org.junit.Test;

import ctdownloader.model.ChangeTaskModel;
import ctdownloader.model.NoTaskContextAvailableException;
import ctdownloader.model.TaskContextModel;
import ctdownloader.util.JsonReader;
import junit.framework.Assert;
import junit.framework.TestCase;

public class JsonReaderTest extends TestCase {
	
	private JsonReader reader;
	
	@Before
	public void setUp(){
		reader = new JsonReader();
	}
	
	
	@Test
	public void testReadTasks(){
		
		ArrayList<ChangeTaskModel> tasks = reader.readTasks("MylynContexts");
		
		System.out.println(tasks.size());
		
	}
	
	public void testReadTaskContexts() throws NoTaskContextAvailableException{
		ArrayList<ChangeTaskModel> tasks = reader.readTasks("MylynContexts");
		for(ChangeTaskModel task : tasks){
			
			ArrayList<TaskContextModel> tcModels = task.getAllTaskContextModels();
			ArrayList<IInteractionContext> tcs =  task.getTaskContexts();
			
			
			assertEquals(tcModels.size(), tcs.size());
			
			
		}
	}

}
