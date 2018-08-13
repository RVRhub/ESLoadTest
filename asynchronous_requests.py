import concurrent.futures
import requests
import logging
import json
import csv

def get_urls():
    return ["http://127.0.0.1:8080/runner/elk/count/","http://127.0.0.1:7000/runner/elk/count/","http://127.0.0.1:9000/runner/elk/count/","http://127.0.0.1:9090/runner/elk/count/"]
#    return ["http://127.0.0.1:8080/runner/elk/transport/count/","http://127.0.0.1:7000/runner/elk/transport/count/","http://127.0.0.1:9000/runner/elk/transport/count/","http://127.0.0.1:9090/runner/elk/transport/count/"]

def run_test(testNum, countOfRequests):
    print('========>>> Run test number ', testNum , ', requsts: ', countOfRequests);
    with concurrent.futures.ThreadPoolExecutor(max_workers=20) as executor:
        futures = executor.map(requests.get, [
            (url+countOfRequests)
            for url in get_urls()
        ])
        startTime = 0;
        finishTime = 0;
        totalRequests = 0;
        failedRequests = 0;
        for response in futures:
            text = response.text;
            print(text);
            data = json.loads(text);
            if data["startTime"] < startTime or startTime == 0:
                startTime = data["startTime"];
            if data["finishTime"] > finishTime:
                finishTime = data["finishTime"];
            totalRequests = totalRequests + data["count"];
            failedRequests = failedRequests + data["failedCalls"];
            pass
        execTime = finishTime-startTime;
        averageTimePerRequest = (execTime/totalRequests)/1e6;
        print( "Average time per request, msec: ", averageTimePerRequest);
        errorCoef =  (failedRequests*100)/totalRequests;
        print( "Error coefficient, %: ", errorCoef);
        execTimeSec = int(execTime/1e9);
        print( "Total Execution time, sec: ", execTimeSec);
        result = [totalRequests,averageTimePerRequest, errorCoef, execTimeSec];
 #       result = [{'avgTimePerReq' : averageTimePerRequest, 'errorCoef' :  errorCoef, 'execTimeSec' :  execTimeSec}];
        return result;

def main():
    test1 = run_test(1,'25');
    test2 = run_test(2,'125');
    test3 = run_test(3,'250');
    test4 = run_test(4,'1250');
    test5 = run_test(5,'2500');
 #   test6 = run_test(5,'10000');
    with  open('result.csv', 'w') as csvfile2:
        writer2 = csv.writer(csvfile2, dialect='excel', quotechar='"', delimiter =';', quoting=csv.QUOTE_ALL);
        writer2.writerow(['requests','avgTimePerReq', 'errorCoef', 'execTimeSec']);
        writer2.writerow(test1);
        writer2.writerow(test2);
        writer2.writerow(test3);
        writer2.writerow(test4);
        writer2.writerow(test5);
    #    writer2.writerow(test6);

main()
