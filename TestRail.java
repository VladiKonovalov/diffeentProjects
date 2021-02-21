import com.gurock.testrail.APIClient;
import com.gurock.testrail.APIException;
import com.jayway.jsonpath.JsonPath;
import java.util.Map;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TestRail {
	private static APIClient client = new APIClient("APICLIENT");
	private static int project_id = 20;
	private static String TestRailUsername = System.getenv("TestRailUsername");
	private static String TestRailPassword = System.getenv("TestRailPassword");
	private static String Suite_id, Section_id, Run_id, Cause_id;

	TestRail() throws MalformedURLException, IOException, APIException {
		System.out.println("----------------------TestRail-----------");
		Suite_id = getSuiteOrCreate();
		System.out.println("Testrail: Created Suite " + Suite_id);
		Section_id = getSection();
		if (Section_id.equals("[]")) {
			Section_id = CreateSection("basic Section");
		}
		System.out.println("Testrail: Created Section " + Section_id);

	}

	public static String getSuiteOrCreate() throws MalformedURLException, IOException, APIException {
		int i = 0;
		client.setUser(TestRailUsername);
		client.setPassword(TestRailPassword);
		JSONArray x = (JSONArray) client.sendGet("get_suites/" + project_id);
		if (x.toJSONString().contains("Api Automation")) {
			while (!JsonPath.parse(x).read("[" + i + "].name").equals("Api Automation")) {
				i++;
			}
		} else {
			Suite_id = CreateSuite();
			
			return Suite_id;
		}
		Suite_id = JsonPath.parse(x).read("["+i+"].id").toString();
		return Suite_id;
	}

	public static String CreateSuite() throws MalformedURLException, IOException, APIException {
		client.setUser(TestRailUsername);
		client.setPassword(TestRailPassword);
		Map<String, String> data = new HashMap<String, String>();
		data.put("name", "Api Automation");
		data.put("description", "This test check the automation of the backend APi  ");
		JSONObject x = (JSONObject) client.sendPost("add_suite/" + project_id, data);
		return x.get("id").toString();

	}

	public static String getSection() throws MalformedURLException, IOException, APIException {
		client.setUser(TestRailUsername);
		client.setPassword(TestRailPassword);
		JSONArray x = (JSONArray) client.sendGet("get_sections/" + project_id + "&suite_id=" + Suite_id);
		if (x.toJSONString().equals("[]")) {
			return x.toJSONString();
		}
		Section_id = JsonPath.parse(x).read("[0].id").toString();
		return Section_id;

	}

	public static String getCause(String title) {
		client.setUser(TestRailUsername);
		client.setPassword(TestRailPassword);
		JSONArray Cause;
		try {
			Cause = (JSONArray) client
					.sendGet("get_cases/" + project_id + "&suite_id=" + Suite_id + "&filter=" + title);

			if (Cause.toJSONString().equals("[]")) {
				return Cause.toJSONString();
			}
			Cause_id = JsonPath.parse(Cause).read("[0].id").toString();
			return Cause_id;
		} catch (IOException | APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "[]";
		}
	}

	public static JSONArray getCauses() {
		client.setUser(TestRailUsername);
		client.setPassword(TestRailPassword);
		JSONArray Cause;
		try {
			Cause = (JSONArray) client.sendGet("get_cases/" + project_id + "&suite_id=" + Suite_id);

//		if (Cause.toJSONString().equals("[]")){
//			return Cause;
//		} 
			return Cause;
		} catch (IOException | APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static JSONArray getTests(String statusId) {
		client.setUser(TestRailUsername);
		client.setPassword(TestRailPassword);
		String filter = "";
		JSONArray Tests;
		try {
			if (statusId != "")
				filter = "&status_id=" + statusId;
			Tests = (JSONArray) client.sendGet("get_tests/" + Run_id + filter);
			return Tests;
		} catch (IOException | APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String CreateSection(String name) throws MalformedURLException, IOException, APIException {
		client.setUser(TestRailUsername);
		client.setPassword(TestRailPassword);
		Map<String, String> data = new HashMap<String, String>();
		data.put("name", name);
		data.put("suite_id", Suite_id);
		data.put("description", "basic Api check");
		JSONObject Section = (JSONObject) client.sendPost("add_section/" + project_id, data);
		return Section.get("id").toString();

	}

	public static String CreateCase(String name, String bool) {
		client.setUser(TestRailUsername);
		client.setPassword(TestRailPassword);
		Map<String, String> data = new HashMap<String, String>();
		data.put("title", name);
		data.put("Checkbox", bool);
		JSONObject x;
		try {
			x = (JSONObject) client.sendPost("add_case/" + Section_id, data);

			System.out.println(x);
			return x.get("id").toString();
		} catch (IOException | APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String CreateRun(String Suite_id, String bool) {
		client.setUser(TestRailUsername);
		client.setPassword(TestRailPassword);
		Map<String, String> data = new HashMap<String, String>();
		data.put("suite_id", Suite_id);
		data.put("name", bool);
		JSONObject x;
		try {
			x = (JSONObject) client.sendPost("add_run/" + project_id, data);

			System.out.println(x);
			return x.get("id").toString();
		} catch (IOException | APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String UpdateRun(String Run_id) {
		client.setUser(TestRailUsername);
		client.setPassword(TestRailPassword);
		Map<String, String> data = new HashMap<String, String>();
		data.put("suite_id", Suite_id);
		data.put("include_all", "true");
		JSONObject x;
		try {
			x = (JSONObject) client.sendPost("update_run/" + Run_id, data);

			System.out.println(x);
			return x.get("id").toString();
		} catch (IOException | APIException e) {
// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String AddResultForCase(String Run_id, String Case_id, String Status, String Comment) {
		client.setUser(TestRailUsername);
		client.setPassword(TestRailPassword);
		Map<String, String> data = new HashMap<String, String>();
		data.put("status_id", Status);
		data.put("comment", Comment);

		JSONObject x;
		try {
			x = (JSONObject) client.sendPost("add_result_for_case/" + Run_id + "/" + Case_id, data);

			System.out.println(x);
			return x.get("id").toString();
		} catch (IOException | APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static void PutCauseId(String case_id) {
		Cause_id = case_id;
	}

	public static void PutRunId(String run_id) {
		Run_id = run_id;
	}

	public static String GetCauseId() {
		return Cause_id;
	}

	public static String GetRunId() {
		return Run_id;
	}

	public static String GetSuite_id() {
		return Suite_id;
	}

	public void BlockTestThatNotInTag() {
		System.out.println("-----------Blocking all the Cause that didnt been checked-------");
		client.setUser(TestRailUsername);
		client.setPassword(TestRailPassword);
		JSONArray Result;
		try {
			JSONArray Causes = getCauses();
			for (int i = 0; i < Causes.size(); i++) {
				Cause_id = JsonPath.parse(Causes).read("[" + i + "].id").toString();
				if (Run_id != null) {
					Result = (JSONArray) client.sendGet("get_results_for_case/" + Run_id + "/" + Cause_id);
					if (Result.toJSONString().equals("[]")) {
						TestRail.AddResultForCase(TestRail.GetRunId(), TestRail.GetCauseId(), "2",
								"Skiped Cause not part of the test");
					}
				}
			}
		} catch (IOException | APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
