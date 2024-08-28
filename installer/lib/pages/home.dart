/*
 * This file is part of Aliucord, an Android Discord client mod.
 * Copyright (c) 2023 Juby210 & Vendicated
 * Licensed under the Open Software License version 3.0
 */

import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';

import '../constants.dart';
import '../icons.dart';
import '../widgets/init_install.dart';
import '../utils/main.dart';
import '../widgets/commits.dart';
import 'settings.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  bool _permissionsGranted = false;
  double? _freeSpace;
  String? _commit;
  String? _supportedVersion;
  String? _supportedVersionName;

  void _selectCommit(String? commit) async {
    if (commit == null) return;
    try {
      final dataUrl = githubAPI!.getDownloadUrl('builds', 'data.json');
      final res = await dio.get(dataUrl);
      final json = jsonDecode(res.data);
      setState(() {
        _commit = commit;
        _supportedVersion = json['versionCode'];
        _supportedVersionName = json['versionName'];
      });
    } on DioException catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Failed to get data from github:\n$e')));
    }
  }

  @override
  void initState() {
    super.initState();
    _checkPermissions();
    _checkKeystore();
  }

  @override
  Widget build(BuildContext context) {
    if (!_permissionsGranted) {
      return Scaffold(
      appBar: AppBar(title: const Text('Zeetcord Installer')),
      body: Center(child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(
            'You need to grant storage permission to use this app.',
            textAlign: TextAlign.center,
            style: Theme.of(context).textTheme.titleLarge
          ),
          Padding(padding: const EdgeInsets.only(top: 10), child: ElevatedButton(
            onPressed: _checkPermissions,
            child: const Text('Grant permission'),
          )),
      ])),
    );
    }

    if (_freeSpace == null) getFreeSpace().then((value) => setState(() => _freeSpace = value));

    return Scaffold(
      appBar: AppBar(
        title: const Text('Zeetcord Installer'),
        actions: [
          Tooltip(message: 'Support server', child: IconButton(
            icon: const Icon(CustomIcons.discord),
            onPressed: () => openUrl('https://discord.gg/$supportServer'),
          )),
          PopupMenuButton<int>(
            onSelected: (action) => Navigator.push(context, MaterialPageRoute(builder: (context) => const SettingsPage())),
            itemBuilder: (context) => [
              const PopupMenuItem(value: 0, child: Text('Settings')),
            ],
          ),
        ],
      ),
      body: Padding(padding: const EdgeInsets.all(4), child: Column(children: [
        _freeSpace != null && _freeSpace! < 500 ? const Card(child: ListTile(
          title: Text('You\'re running on low space'),
          subtitle: Text('Installation may fail due to not enough free space'),
          leading: Icon(Icons.data_usage, color: Colors.red, size: 40),
        )) : const SizedBox.shrink(),
        Card(child: ListTile(
          title: const Text('Zeetcord'),
          subtitle: RichText(text: TextSpan(
            style: Theme.of(context).textTheme.bodyMedium,
            text: 'Supported version: ',
            children: _supportedVersionName == null ? null : [
              TextSpan(text: _supportedVersionName, style: const TextStyle(fontWeight: FontWeight.bold))
            ],
          )),
          trailing: TextButton(
            onPressed: _commit == null ? null : () => initInstall(context, _commit!, _supportedVersion!),
            child: const Row(
              mainAxisSize: MainAxisSize.min,
              children: [ Icon(Icons.archive_outlined), Text(' Install') ],
            ),
          ),
        )),
        Flexible(child: CommitsWidget(selectCommit: _selectCommit)),
      ])),
      bottomNavigationBar: NavigationBar(
        destinations: const [
          NavigationDestination(icon: Icon(Icons.home), label: 'Home'),
          NavigationDestination(icon: Icon(Icons.extension), label: 'Plugins'),
        ],
        onDestinationSelected: (page) => page == 1 ? toast('Plugins page is not done yet.') : null,
      ),
    );
  }

  void _checkPermissions() {
    checkPermissions().then((res) {
      if (_permissionsGranted != res) {
        setState(() => _permissionsGranted = res);
      }
    });
  }

  void _checkKeystore() {
    checkKeystoreDeleted().then((res) {
      if (res) {
        showDialog(context: navigatorKey.currentContext!, barrierDismissible: false, builder: (context) => AlertDialog(
          title: const Text('Keystore Missing'),
          content: const Text('Zeetcord is installed, but the keystore is missing. This likely means you deleted "ks.keystore" in the Zeetcord folder. As a consequence, you can not update Aliucord directly and must instead first uninstall the old Aliucord.'),
          actions: [
            TextButton(
              child: const Row(
                mainAxisSize: MainAxisSize.min,
                children: [ Icon(Icons.delete_outlined), Text(' Uninstall') ],
              ),
              onPressed: () async => await uninstallAliucord(),
            ),
          ],
        ));
      }
    });
  }
}
