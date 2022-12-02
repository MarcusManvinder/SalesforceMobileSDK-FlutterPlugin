import 'dart:async';
import 'dart:convert';
import 'package:flutter/services.dart';

class SalesforcePlugin {
  static String apiVersion = 'v55.0';

  static const _channel = const MethodChannel('sfplugin');

  static Future<String?> get platformVersion {
    // _channel.invokeMethod('getPlatformVersion');
    try {
      return _channel.invokeMethod<String>('getPlatformVersion');
    } on PlatformException catch (e) {
      throw ArgumentError('Unable to get platform version: ${e.message}');
    }
  }

  // ignore: slash_for_doc_comments
  /**
   * Send arbitrary force.com request
   * @param endPoint
   * @param path
   * @param method
   * @param payload
   * @param headerParams
   * @param fileParams  Expected to be of the form: {<fileParamNameInPost>: {fileMimeType:<someMimeType>, fileUrl:<fileUrl>, fileName:<fileNameForPost>}}
   * @param returnBinary When true response returned as {encodedBody:"base64-encoded-response", contentType:"content-type"}
   */
  static Future<Map> sendRequest(
      {String endPoint: "/services/data",
      required String path,
      String method: "GET",
      Map? payload,
      Map? headerParams,
      Map? fileParams,
      bool returnBinary: false}) async {
    final Object response =
        await _channel.invokeMethod('network#sendRequest', <String, dynamic>{
      'endPoint': endPoint,
      'path': path,
      'method': method,
      'queryParams': payload,
      'headerParams': headerParams,
      'fileParams': fileParams,
      'returnBinary': returnBinary
    });
    return response is Map ? response : json.decode(response.toString());
  }

  static Future<Map> query(String soql) =>
      sendRequest(path: "/${apiVersion}/query", payload: {'q': soql});

  static Future<Map> post(Map params) => sendRequest(
        path: "/${apiVersion}/sobjects/MetricsEntry__c",
        method: "POST",
        payload: params,
        fileParams: Map(),
      );

  static Future<Map> delete(String id) => sendRequest(
        path: "/${apiVersion}/sobjects/MetricsEntry__c/${id}",
        method: "DELETE",
        payload: Map(),
        fileParams: Map(),
      );

  static Future<Map> patch(String id, Map params) => sendRequest(
        path: "/${apiVersion}/sobjects/MetricsEntry__c/${id}",
        method: "PATCH",
        payload: params,
        fileParams: Map(),
      );

  static Future<Map> getClientInfo() async {
    final Object response =
        await _channel.invokeMethod('network#getClientInfo');
    return response is Map ? response : json.decode(response.toString());
  }
}
