JavaDecisionTree
================

Just a simple example of a Decision Tree growing algorithm written in Java.

This is built with help from the book Artificial Intelligence a Modern Approach 3e (2009) by Stuart Russel and Peter Norvig
Which is a decision tree built on Information Gain.

When conditions, attributes, and examples are entered:

```Java
        // conditionas are possible values for attributes
		Condition yes = new Condition("yes");
		Condition no = new Condition("no");
		Condition some = new Condition("some");
		Condition full = new Condition("full");
		Condition none = new Condition("none");
		Condition $ = new Condition("$");
		Condition $$ = new Condition("$$");
		Condition $$$ = new Condition("$$$");
		Condition french = new Condition("french");
		Condition thai = new Condition("thai");
		Condition burger = new Condition("burger");
		Condition italian = new Condition("italian");
		Condition zero10 = new Condition("zero10");
		Condition ten30 = new Condition("ten30");
		Condition thirty60 = new Condition("thirty60");
		Condition sixtyup = new Condition("sixtyup");
		
		// these attributes are overall, all possible values
		Attribute[] real_attributes = new Attribute[10];
		real_attributes[0] = new Attribute("alt", yes, no);
		real_attributes[1] = new Attribute("bar", yes, no);
		real_attributes[2] = new Attribute("fri", yes, no);
		real_attributes[3] = new Attribute("hun", yes, no);
		real_attributes[4] = new Attribute("pat", some, full, none);
		real_attributes[5] = new Attribute("price", $, $$, $$$);
		real_attributes[6] = new Attribute("rain", yes, no);
		real_attributes[7] = new Attribute("res", yes, no);
		real_attributes[8] = new Attribute("type", french, thai, burger, italian);
		real_attributes[9] = new Attribute("est", zero10, ten30, thirty60, sixtyup);
		
		// these attributes will be linked to examples	
		Attribute[] example_attributes = new Attribute[11];
		example_attributes[0] = new Attribute("alt", yes, no);
		example_attributes[1] = new Attribute("bar", yes, no);
		example_attributes[2] = new Attribute("fri", yes, no);
		example_attributes[3] = new Attribute("hun", yes, no);
		example_attributes[4] = new Attribute("pat", some, full, none);
		example_attributes[5] = new Attribute("price", $, $$, $$$);
		example_attributes[6] = new Attribute("rain", yes, no);
		example_attributes[7] = new Attribute("res", yes, no);
		example_attributes[8] = new Attribute("type", french, thai, burger, italian);
		example_attributes[9] = new Attribute("est", zero10, ten30, thirty60, sixtyup);
		example_attributes[10] = new Attribute("willwait", yes, no); // this is the one we are checking for in the tree
		
		// examples contain attributes and a value
		// here we will build a decision tree based on will wait for a seat at a restaurant
		Example[] examples = new Example[12];
		// these are in the order of the example attributes above
		examples[0] = new Example(example_attributes, yes, no, no, yes, some, $$$, no, yes, french, zero10, yes);
		examples[1] = new Example(example_attributes, yes, no, no, yes, full, $, no, no, thai, thirty60, no);
		examples[2] = new Example(example_attributes, no, yes, no, no, some, $, no, no, burger, zero10, yes);
		examples[3] = new Example(example_attributes, yes, no, yes, yes, full, $, yes, no, thai, ten30, yes);
		examples[4] = new Example(example_attributes, yes, no, yes, no, full, $$$, no, yes, french, sixtyup, no);
		examples[5] = new Example(example_attributes, no, yes, no, yes, some, $$, yes, yes, italian, zero10, yes);
		examples[6] = new Example(example_attributes, no, yes, no, no, none, $, yes, no, burger, zero10, no);
		examples[7] = new Example(example_attributes, no, no, no, yes, some, $$, yes, yes, thai, zero10, yes);
		examples[8] = new Example(example_attributes, no, yes, yes, no, full, $, yes, no, burger, sixtyup, no);
		examples[9] = new Example(example_attributes, yes, yes, yes, yes, full, $$$, no, yes, italian, ten30, no);
		examples[10] = new Example(example_attributes, no, no, no, no, none, $, no, no, thai, zero10, no);
		examples[11] = new Example(example_attributes, yes, yes, yes, yes, full, $, no, no, burger, thirty60, yes);
'''

which will produce a tree like follows

'''
Node: middle node  value: pat children: 
-Node: second leaf some value: yes children: no children.
-Node: middle node full value: hun children: 
--Node: middle node yes value: type children: 
---Node: first leaf french value: no children: no children.
---Node: middle node thai value: fri children: 
----Node: second leaf yes value: yes children: no children.
----Node: second leaf no value: no children: no children.
---Node: second leaf burger value: yes children: no children.
---Node: second leaf italian value: no children: no children.
--Node: second leaf no value: no children: no children.
-Node: second leaf none value: no children: no children.
'''

which as an image looks as follows

![](http://i.imgur.com/gkGHtBo.png)