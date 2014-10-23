/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

grammar mygrammar;
IF : '���';
THEN: '��';
AND : '��';
OR  : '��';
GT : '����';
LT : 'С��';
ET : '����';
LK : '(';
RK : ')';
BLK: '{';
BRK: '}';
D : [0-9]+;
UNIT : '��';
ID : ('a'..'z'|'A'..'Z')+;
//U_LETTER : ('\u4f00'..'\u9fa5')+;//|'\uF900'..'\uFAFF'|'\uFE30'..'\uFE4F')+;
ACTION : '������';
simple:IF condition THEN ACTION;
condition : expression ((AND|OR) expression)*;
expression :LK condition RK 
            |check item;
check : GT(ET)*|LT(ET)*|ET;
item : D UNIT ID;
WS : [ \t\r\n]+ -> skip ;