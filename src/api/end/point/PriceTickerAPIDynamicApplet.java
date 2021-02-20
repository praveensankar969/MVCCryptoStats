package api.end.point;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PriceTickerAPIDynamicApplet {

	static Map<String, Float> initialData;
	static List<Float> initialPrice_;

	public static List<ApiTemplateTicker> GetData() throws IOException, ParseException {

		List<ApiTemplateTicker> data = new ArrayList<ApiTemplateTicker>();
		URL url = new URL("https://api.wazirx.com/api/v2/tickers");
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		int resCode = connection.getResponseCode();
		StringBuffer response = new StringBuffer();

		if(resCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while((readLine = in.readLine()) != null) {
				response.append(readLine);
			} in.close();
		}
		else {
			System.out.println("Error");
			System.exit(0);
		}

		String responseData = response.toString();
		String[] arrayData = responseData.split("}");

		String tempText = arrayData[0].substring(1);
		int index = tempText.indexOf("{");
		int indexNext = tempText.indexOf("open")-2;
		tempText= tempText.substring(index, indexNext)+"}";
		JSONParser parser = new JSONParser();  
		JSONObject json = (JSONObject) parser.parse(tempText); 
		ObjectMapper objectMapper = new ObjectMapper(); 
		ApiTemplateTicker obj = null;

		try { 
			obj = objectMapper.readValue(json.toJSONString(),ApiTemplateTicker.class );

		}catch(Exception e) {

			e.printStackTrace(); }

		data.add(obj);


		for(int i=1;i<arrayData.length;i++) {

			tempText = arrayData[i];
			index = tempText.indexOf("{");
			indexNext = tempText.indexOf("open")-2;
			tempText= tempText.substring(index, indexNext)+"}";
			parser = new JSONParser();  
			json = (JSONObject) parser.parse(tempText); 
			objectMapper = new ObjectMapper(); 

			try { 
				obj = objectMapper.readValue(json.toJSONString(),ApiTemplateTicker.class );

			}catch(Exception e) {

				e.printStackTrace(); }

			data.add(obj);



		}

		return data;


	}

	public static Map<String, Float> SaveInitialData(List<ApiTemplateTicker> data) {

		Map<String, Float> price = new HashMap<String, Float>();
		for(int i=0;i<data.size();i++) {
			if(data.get(i).getQuote_unit().equals("usdt")) {
				price.put(data.get(i).getBase_unit(), data.get(i).getLast());
			}
		}

		return price;
	}



	public static void GettingDifference(List<Float> initialPrice, Map<String, Float> initial) throws InterruptedException, IOException, ParseException {


		while(1>0) {
			List<Float> currentPrice = null;
			List<ApiTemplateTicker> currentData = GetData();
			Map<String, Float> currentDataMap = SaveInitialData(currentData);
			currentPrice = new ArrayList<>(currentDataMap.values());
			System.out.println("Check Point Timeout 30 seconds at " + java.time.LocalTime.now());
			String coin;
			String setText_="";
			float changePercentage;

			for(int i=0;i<currentPrice.size();i++) {


				if(currentPrice.get(i)- initialPrice.get(i) >0) {
					changePercentage = ((currentPrice.get(i)- initialPrice.get(i))/initialPrice.get(i))*100;
					coin  = GetKey(initial,initialPrice.get(i));
					if(changePercentage>0.5) {
						setText_ += " <tr><td><font color='green'>"+coin +"</font></td><td>"+initialPrice.get(i)+"</td><td>"+currentPrice.get(i)+"</td><td><font color='green'>"+changePercentage+"</font></td></tr>";
					}
					else if(changePercentage>3) {
						setText_ += " <tr><td><font color='red'>"+coin +"</font></td><td>"+initialPrice.get(i)+"</td><td>"+currentPrice.get(i)+"</td><td><font color='red'>"+changePercentage+"</font></td></tr>";
					}

				}
				System.out.println(setText_);
			}
			Thread.sleep(10000);
		}
	}


	public static String GetKey(Map<String, Float> data, float value) {

		for(Entry<String, Float> entry : data.entrySet()) {
			if(entry.getValue() == value) {
				return entry.getKey();
			}
		}

		return null;

	}


	public static int CountZero(float price) {

		int zero = 0;
		while(price<1) {
			price = price*10;
			zero++;
		}
		zero--;

		return zero;
	}

	//public static void main(String args[]) throws IOException, InterruptedException, ParseException {


	//}

	public static void Main() throws IOException, ParseException, InterruptedException {

		List<ApiTemplateTicker> initialPrice = GetData();
		initialData = SaveInitialData(initialPrice);
		initialPrice_ = new ArrayList<>(initialData.values());

	}

	public static void Result() throws InterruptedException, IOException, ParseException {

		GettingDifference(initialPrice_, initialData);


	}
}
