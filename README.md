Task 1.

The program reports three attempts. "abc" is not a number, so it becomes null. "200" is a number, but it is outside the allowed range 0..120, so takeIf also makes it null. "35" is inside the range, so it is accepted.

With inline, main has no call to ask and no Ref.IntRef. The body of ask and the body of the lambda are copied into main. So attempts is just a normal local int. Without inline, main calls ask and creates a Ref.IntRef. The lambda is a separate object, and it changes the outer variable attempts. A local variable cannot be shared by reference, so Kotlin puts it inside a small wrapper object. Both main and the lambda change the value inside that wrapper.

Inlining a 200-line function is usually a bad idea. Inline helps small functions that are called often, because it removes lambda objects and calls. A big function is copied to every call, so the program becomes much larger.

Task 2.

The red line is pending.add(reminder). The compiler says that reminder is an inline parameter and that noinline must be added. The fix is noinline reminder: () -> Unit.

With "Write report", the program first prints "Saved: Write report", then "Pending reminders: 1", then "Reminder: finish 'Write report'". The order is save, store, print the count, run the reminder.

If pending.add(save) is added, the compiler gives the same error for save and asks for noinline. save can be called because its body is copied into addTask. It cannot be stored because an inline parameter is not a real object. A list can store only a real function object.

The reminder lambda is a real object because it is marked noinline. It is stored in the list and is not copied into main. If it were inline, there would be no object to store, so the reminder could not run later.

Task 3.

findFirst prints only one line because return leaves the whole findFirst function after the first match. findAll prints every match because return@forEach only skips the current item and then continues with the next one.

return@forEach works like continue in a normal for loop.

After a match, the line "No contact contains ..." is not printed because findFirst has already returned. "Search finished" is still printed because the return leaves only findFirst, not main.

Without inline on each, the compiler says "'return' is prohibited here." The normal forEach is inline, so its lambda is copied into the caller and can return from the caller. The function each is not inline, so its lambda is a separate object and cannot return from findWithEach.

After adding inline to each, the error goes away. A bare return is allowed in a lambda when the lambda is inlined into the function that calls it, and it is not noinline or crossinline.

Task 4.

The compiler names the parameter action and suggests crossinline. The fix is inline fun command(crossinline action: () -> Unit).

Input 2 prints "Goodbye!", input 9 prints "Unknown command", and input 4 prints "Settings opened". All four commands work.

If the first command contains return, the compiler says "'return' is prohibited here." In Task 3 the lambda is called directly inside the inline forEach, so the return can leave findFirst. Here action is put inside a Runnable object and is called later by Runnable.run. That is outside the inline function, so a return has no function to leave. crossinline forbids this kind of return.

Task 5.

With 10, abc, 25.5, done, abc is skipped and the result is "Items: 2, total: 35.5". With 10, abc, 20000, 5, abc is skipped, then the program prints "Price 20000.0 is too large, stopping" and ends with "Items: 1, total: 10.0". The 5 is never read because break leaves the loop immediately.

The continue inside the lambda passed to orSkip, originally run, skips the rest of the current while step. The break inside the lambda passed to let leaves the while loop. The first break after the done check is directly in the loop, not in a lambda.

Without inline on orSkip, the compiler says "'break' or 'continue' crosses a function or class boundary." The normal run is inline, so it allows the jump. A normal function gets a lambda object, and a jump cannot cross that function boundary.

After adding inline to orSkip, the error goes away. A break or continue is allowed in a lambda when the lambda is inlined and its call is inside the loop that the jump should leave or continue.

Task 6.

In the second run, age is null because "x" is not an Int. Height is null because "1,75" has a comma, but Kotlin expects a dot. Subscribed is null because toBooleanStrictOrNull accepts true or false, not "yes". Phone is null because the phone line is empty. City is not null because it is an empty String. The String branch accepts every text, even an empty one.

Without reified, the compiler shows two errors, both on T::class. One is in when (T::class), and the other is in T::class.simpleName inside the error message. The as T? casts also use T, but they are unchecked casts and do not need reified. The function needs reified because it asks for the real class of T at run time.

readValue<Char> compiles, but it fails at run time with "IllegalStateException: Unsupported type Char". The message comes from the else branch that calls error.

Long support is added in the when, and 998901234567 is read correctly.

Task 7.

10 and 7 go to the Int list. 2.5 and 0.75 go to the Double list. apples and pears go to the String list. 7 is an Int and not a Double because parseToken tries toIntOrNull first. Only if that fails does it try toDoubleOrNull.

Without reified, the compiler says that it cannot check for an instance of the erased type T. Erased means the real type is not kept at run time. The program only knows that it has a list, not that T is Int or String. reified puts the real type into the copied function at each call.

filterIsInstance<Int> gives the same result as onlyOfType<Int>. It is declared with two modifiers: inline and reified.

After adding Boolean parsing, the input true 5 false x gives All [true, 5, false, x], Ints [5], Doubles [], Words [x] and Booleans [true, false].

Task 8.

The program prints 112.00 for 100, four words for "the quick  brown fox", and 5120 bytes for 5 KB. The sentence has four words because the empty space between the two spaces is ignored by isNotBlank.

Double.withVat is inline as a whole property. String.wordCount is inline only on its getter. For a val, both work the same when you read it, because the getter is copied to the call.

Adding inline val taxRate: Double = 0.12 gives "inline property cannot have a backing field." An inline getter is copied into the caller, so it cannot read a real field. A property with an initial value needs a field. That is why only a computed property without a backing field can be inline.

Int.kilobytes returns this * 1024 and prints the number of bytes for the typed number.

Task 9.

With private, the error is "public-API inline function cannot access non-public-API property." The inline function debug is copied into other modules, so its body can use only declarations that those modules can access.

Changing private to internal does not remove the error. internal is still not public API for another module.

Adding @PublishedApi above internal var debugEnabled makes it compile. The annotation makes the internal declaration usable by inlined code from other modules. With yes, the output is "(building the report...)", then "[DEBUG] report with 1000 rows", then "Program finished". With no, only "Program finished" is printed because the lambda is never called.

If debug is changed to a normal function with a String parameter and called as debug(buildReport()), the text "(building the report...)" is printed even with no. The argument is built before the function call, whether debug is on or off. The lambda version builds the message only when it is needed.

Task 10.

The line "bad" is rejected because it has only one part, so parts.size != 2. The line "Bobur x" is rejected because "x".toIntOrNull() returns null. Both print "Use: name grade".

The IDE shows book as Gradebook. The long type is MutableMap<String, MutableList<Int>>. With the Grades alias, it can be written as MutableMap<String, Grades>.

Changing Gradebook.addGrade to MutableMap<String, MutableList<Int>>.addGrade still works. An alias is only a shorter name for the same type. It does not create a new type, so the alias and the long type can be used in place of each other.

Grades.best() uses maxOrNull(), and the program prints the best grade for every student.

Task 11.

The plain lambda given to a Validator parameter is check("Not blank", email) { it.isNotBlank() }. A Validator stored in a plain function type is val plain: (String) -> Boolean = isEmail. Both work because Validator is only another name for (String) -> Boolean. The compiler replaces the alias with the real type.

roomDemo passes feet to describeRoom, which expects meters. The compiler does not complain because Meters and Feet are both just Double. The program prints 13.12336 m even though the value was feet. A value class could protect this. Separate value classes for Meters and Feet would be different types even though both contain a Double.

Rule is Predicate<String>, and startsWithCode checks startsWith("+998"). The phone demo prints how many of the three numbers start with +998.

Task 12.

By default, println(a) prints something like task12.Contact@2cdf8d8a. This is the class name, @ and the hash code. It comes from the default Any.toString.

a == b is false because == calls equals. The default equals compares references, just like ===. The phone numbers are equal, but a and b are two different objects.

With toString, equals and hashCode overridden, the output changes. println(a) prints Ali (991234567), a == b is true, a === b is false, and book contains b is true. equals now compares phone numbers, but === still compares objects.

If hashCode is deleted, a == b is still true, but book contains b is false. The default hashCode is based on the object, so equal objects get different hashes. A HashSet then looks in the wrong place. If two objects are equal, they must have the same hash code.

equals takes Any? because that is the method from Any. The argument can be any object or null. other is Contact is false when other is null, so it is safe.

Task 13.

volume uses the Int branch, darkMode uses the Boolean branch, username uses the String branch, theme uses the null branch, and ratio uses the Double branch. A missing name prints "No such setting" and returns before the cast.

The line as String works only for username and prints Dilnoza. For volume it throws ClassCastException because Integer cannot become String. For darkMode it throws ClassCastException because Boolean cannot become String. For theme it throws NullPointerException because null cannot become a non-null String. For ratio it throws ClassCastException because Double cannot become String.

With as? String, volume, darkMode, theme and ratio give null, and username gives Dilnoza. With as String?, volume, darkMode and ratio still throw ClassCastException, theme gives null, and username gives Dilnoza. as? returns null when the cast fails. as String? only allows null as a result, but the cast itself is still unsafe.

typeName can be used on every non-null value because every class inherits from Any. For example, 5.typeName() gives Int and "x".typeName() gives String. A nullable value needs ?. or a nullable receiver.

After adding the Double branch, ratio is described as a decimal and is not an unknown type.

Task 14.

With 20 the program prints "You will be 21 next year". With abc it prints "Input error: Age must be a whole number". With -5 it prints "Input error: Age cannot be negative". The exception is thrown by fail and caught in main.

The type of age is Int, not Int?. toIntOrNull() gives Int?, but the right side of ?: is fail, which returns Nothing. Nothing is a subtype of every type and has no value. So the result of the Elvis expression is a non-null Int.

If Nothing is changed to Unit, the compiler reports an unresolved compareTo for age < 0 and a return type mismatch, expected Int but actual Any. age becomes Any because that is the common supertype of Int? and Unit, so it cannot be compared with 0 or returned as Int.

If fail("always") and println("after fail") are added before the return, the IDE marks println("after fail") as unreachable. A call that returns Nothing never finishes normally, so the next line cannot run.

TODO() and error() both return Nothing because they always throw.

Task 15.

runCalculator can return Nothing because the while (true) loop never ends normally. The only way out is exitProcess(0), which also returns Nothing. So the function has no normal return.

The line println("This line is never reached") is never reached. The IDE marks it as unreachable because runCalculator never returns.

The when in discountFor has Int, Int and Nothing branches, so its type is Int. student gives 20, friend gives 10, and zzz throws IllegalStateException: Unknown coupon 'zzz'.

List<Nothing> can be assigned to List<String> and List<Int> because Nothing is a subtype of every type. List is also covariant, written List<out T>, so List<Nothing> is a subtype of both. The list is empty, so this is safe.

reset sets the total to 0. mul N multiplies the total by N. For example, add 5, sub 2, show, mul 3, show, reset, show prints 3, 9 and then 0.
