import 'package:flutter/material.dart';
import 'package:sfplugin/sfplugin.dart';

class Contact {
  final String name;
  final String email;

  const Contact({required this.name, required this.email});
}

class ContactListItem extends ListTile {
  ContactListItem(Contact contact)
      : super(
            title: new Text(contact.name),
            subtitle: new Text(contact.email),
            leading: new CircleAvatar(child: new Text(contact.name[0])));
}

class ContactList extends StatelessWidget {
  final List<Contact> contacts;

  ContactList(this.contacts);

  @override
  Widget build(BuildContext context) {
    return new ListView.builder(
        padding: new EdgeInsets.symmetric(vertical: 8.0),
        itemBuilder: (BuildContext context, int index) =>
            new ContactListItem(contacts[index]),
        itemCount: contacts.length);
  }
}

class ContactsPageState extends State<ContactsPage> {
  late List<Contact> contacts;

  @override
  void initState() {
    super.initState();
    setState(() => this.contacts = []);
    fetchData(); // asynchronous
  }

  void fetchData() async {
    Map response = await SalesforcePlugin.query(
        "SELECT Id, Name, Email FROM User LIMIT 5");
    debugPrint("Response: $response");
    // List<Contact> contacts = response["records"].map((record) {
    //   //debugPrint("record: $record['Name'], $record['Email]");
    //   return Contact(name: record["Name"], email: record["Email"]);
    // }).toList();
    (response["records"] as List).forEach((record) {
      contacts.add(Contact(name: record["Name"], email: record["Email"]));
    });
    debugPrint("contacts: ${contacts.length}");
    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (mounted) {
      setState(() => this.contacts = contacts);
    }
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
        appBar: new AppBar(
          title: new Text("Contacts"),
        ),
        body: new ContactList(this.contacts));
  }
}

class ContactsPage extends StatefulWidget {
  @override
  ContactsPageState createState() => new ContactsPageState();
}

void main() {
  runApp(new MaterialApp(
      title: 'Flutter Demo',
      theme: new ThemeData(primarySwatch: Colors.blue),
      home: new ContactsPage()));
}
