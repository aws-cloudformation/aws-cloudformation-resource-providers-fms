# AWS::FMS::NotificationChannel

Designates the IAM role and Amazon Simple Notification Service (SNS) topic that AWS Firewall Manager uses to record SNS logs.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::FMS::NotificationChannel",
    "Properties" : {
        "<a href="#snsrolename" title="SnsRoleName">SnsRoleName</a>" : <i>String</i>,
        "<a href="#snstopicarn" title="SnsTopicArn">SnsTopicArn</a>" : <i>String</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::FMS::NotificationChannel
Properties:
    <a href="#snsrolename" title="SnsRoleName">SnsRoleName</a>: <i>String</i>
    <a href="#snstopicarn" title="SnsTopicArn">SnsTopicArn</a>: <i>String</i>
</pre>

## Properties

#### SnsRoleName

A resource ARN.

_Required_: Yes

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>1024</code>

_Pattern_: <code>^([^\s]+)$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SnsTopicArn

A resource ARN.

_Required_: Yes

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>1024</code>

_Pattern_: <code>^([^\s]+)$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the SnsTopicArn.
