import 'cs50sdkupdate_platform_interface.dart';

class Cs50sdkupdate {
  Future<String?> getPlatformVersion() {
    return Cs50sdkupdatePlatform.instance.getPlatformVersion();
  }

  Future<void> openPicc() async {
    await Cs50sdkupdatePlatform.instance.openPicc();
  }

  Future<String?> piccCheck() async {
    return await Cs50sdkupdatePlatform.instance.piccCheck();
  }

  Future<String?> piccPolling() async {
    return await Cs50sdkupdatePlatform.instance.piccPolling();
  }

  Future<String?> piccCommand(List<int> apduSend) async {
    return await Cs50sdkupdatePlatform.instance.piccCommand(apduSend);
  }

  Future<String?> piccApduCmd(List<int> pucInput) async {
    return await Cs50sdkupdatePlatform.instance.piccApduCmd(pucInput);
  }

  Future<void> piccClose() async {
    await Cs50sdkupdatePlatform.instance.piccClose();
  }

  Future<bool> piccRemove() async {
    return await Cs50sdkupdatePlatform.instance.piccRemove();
  }

  Future<String?> piccSamAv2Init(int samSlotNo, List<int> samHostKey) async {
    return await Cs50sdkupdatePlatform.instance.piccSamAv2Init(samSlotNo, samHostKey);
  }

  Future<String?> piccHwModeSet(int mode) async {
    return await Cs50sdkupdatePlatform.instance.piccHwModeSet(mode);
  }

  Future<String?> piccM1Authority(int type, int blkNo, List<int> pwd, List<int> serialNo) async {
    return await Cs50sdkupdatePlatform.instance.piccM1Authority(type, blkNo, pwd, serialNo);
  }

  Future<String?> piccNfc(List<int> nfcDataLen, List<int> technology, List<int> nfcUid, List<int> ndefMessage) async {
    return await Cs50sdkupdatePlatform.instance.piccNfc(nfcDataLen, technology, nfcUid, ndefMessage);
  }
}