<?php
	header_remove("X-Powered-By");
	header("Content-type: none");
	header_remove("Content-type");
	require_once("libs/LIB_http.php");
	require_once("libs/LIB_parse.php");

	$email = htmlentities($argv[1]);
	$password = htmlentities($argv[2]);
	$target = htmlentities($argv[3]); //testing url: google.com.tw
	$engineering_teach_url = htmlentities($argv[4]); //nttu login auth url: www.gstatic.com
	$securelogin_url = htmlentities($argv[5]); //nttu login auth url: securelogin.arubanetworks.com
	
	if($securelogin_url != "cannot-find-ip") {
		$securelogin_url = "https://" . $securelogin_url . "/cgi-bin/login"; //https://securelogin.arubanetworks.com/cgi-bin/login
	}
	
	$target = "https://" . $target; //https://google.com.tw
	$engineering_teach_url = "http://" . $engineering_teach_url . "/generate_204"; //http://www.gstatic.com/generate_204
	
	if($email == null || $password == null) {
		echo "請輸入信箱與密碼";
		exit;
	}
	
	if(!check_auth()) {
		echo "不需要驗證";
	}
	else {
		auth_nttu($email, $password);
	}
	
	//ssid: ap-nttu,CSE,ntou,nctu and etc.
	
	function check_auth() {
		global $target;
		
		$response = http_get($target, $ref = "");
		$web_page = $response["FILE"];
		
		if(stristr($web_page, "<title>Google</title>")) {
			return false;
		}
		else {
			return true;
		}
	}

	function auth_ntou($str, $email, $password) {

		global $target;
		
		if($str == "ntou") {
			//ntou(libraries)
			$data_arr = array();
			$data_arr["username"] = $email;
			$data_arr["password"] = $password;
			$data_arr["ok"] = "登入";
			
			$response = http($target = "https://140.121.40.253/user/user_login_auth.jsp?", $ref = "", $method = "POST", $data_arr, EXCL_HEAD);
			
			if($response["ERROR"]=="") {
				http_get("https://140.121.40.253/user/user_login_auth.jsp?", $ref = "");
				http_get("https://140.121.40.253/user/_allowuser.jsp?", $ref = "");
				$web_page = http_get($target, $ref = "");
				$web_page = $web_page["FILE"];

				if(stristr($web_page, "Authentication Required for Wireless Access")) {
					echo "驗證失敗";
				}
				else
					echo "驗證成功";
			}
			else {
				echo "驗證時發生錯誤(海大)";
			}
		}
		else {
			//SSID: TANetRoaming,ntou-guest
		}
	}

	function auth_nttu($email, $password) {
		global $target;
		global $engineering_teach_url;
		
		$web_page = http_get($target, $refer = "");
		$web_page = $web_page["FILE"];

		if(stristr($web_page, "台東大學無線網路驗證系統")) {
			$data_arr = array();
			//user input username and password
			$data_arr["username"] = $email;
			$data_arr["password"] = $password;
			$data_arr["4Tredir"] = $target;
			$parse_arr = parse_array($web_page," <input ",">");
			foreach ($parse_arr as $value) {
				if(stristr($value, "magic"))
					$magic = trim(get_attribute($value, "value"));
			}
		
			$action = "";
			$check_common = "";
			$action_arr = array("library"=>"http://10.1.230.254:1000/fgtauth?".$magic, 
				"engineering_teach"=>$engineering_teach_url);
		
			foreach ($action_arr as $key => $value) {
				$web_page = http_get($value, $refer = "");
				if($web_page!="")
				{
					$action = $value;
					$check_common = $key;
				}
			}

			$method = "POST";
			$ref = "";
			$data_arr["magic"] = $magic;
			$response = http($action, $ref , $method, $data_arr, EXCL_HEAD);
			if($response["ERROR"]=="") {
				$web_page = http_get($target, $refer = "");
				$web_page = $web_page["FILE"];

				if(stristr($web_page, "台東大學無線網路驗證系統")) {
					echo "驗證失敗";
				}
				else
					echo "驗證成功";
			}
		}
		else if(stristr($web_page, "USERNAME")) {
			$action = $securelogin_url;
			$data_arr = array();
			$data_arr["user"] = $email;
			$data_arr["password"] = $password;
			$data_arr["authenticate"] = "authenticate";
			$data_arr["accept_aup"] = "accept_aup";
			$data_arr["requested_url"] = "";
			$method = "POST";
			$ref = "";
			
			if($action != "cannot-find-ip") {
			
				$response = http($action, $ref , $method, $data_arr, EXCL_HEAD);
				if($response["ERROR"]=="") {
					$web_page = http_get($target, $refer = "");
					$web_page = $web_page["FILE"];

					if(stristr($web_page, "USERNAME")) {
						echo "驗證失敗";
					}
					else
						echo "驗證成功";
				}
			}
		}
		else {
			auth_ntou("ntou", $email, $password);
		}
	}
?>
