# CARET

[CARET](https://caretpro.github.io/) is a conversational assistant for Java software development, which implements an architecture open, accountable, and trustworthy. This tool is a plugin for the Eclipse IDE that supports code completion, documentation, code optimisation, error fixing and unit testing. It supports different LLM technologies, which can be added through an extension point, and also allows the user to add new tasks and validators.

## Offline Experiment: Code Completion

This experiment is about the evaluation of CARET assistant to complete Java classes for four projects from [JavaBench](https://github.com/java-bench/JavaBench): PA19, PA20, PA21, and PA22. The corresponding solutions are the projects: PA19-Solution, PA20-Solution, PA21-Solution, and PA22-Solution. The execution tests are performed on the projects PA19-Execution, PA20-Execution, PA21-Execution, and PA22-Execution.

Five agents from different technologies were used in the executions: GPT-4.1-mini, GPT-4.1-nano, Codex-mini-latest, Gemini-2.0-flash, and DeepSeek-chat (DeepSeek-V3-0324). 

In each execution, the four projects are evaluated using a predefined LLM agent, and the results are stored in an "execution-" folder. This folder contains the following files/folders:
- classes.csv: list of results by class.
- log.txt: execution logs.
- xlsx file: summary of the execution results.
- Folders containing the modified projects with each completed class.

The projects_data folder includes the projects used for the evaluation: 
- PA19, PA20, PA21, and PA22: contain incomplete classes.
- -Execution projects: where the executions are performed.
- -Solution projects: contain correctly completed classes.

The results folder includes subfolders grouped by strategy, configuration, and LLM.

```
offline_experiment
├── projects_data/
└── results/
	├── holistic/
	│   ├── max-context/
	│   └── min-context/
	└── sequential/
		├── max-context/
		└── min-context/
```

## User Study: New Task Definition

This experiment is a user study of CARET assistant, in which the user must define a new assistive task in the CARET assistant and then test its functionality. The objective of this new task is to transform the body of a given Java method so that it complies with a specific set of programming style rules.

After defining and testing the new task, the participants completed a SUS questionnaire to evaluate the usability of the CARET assistant. The CARET_ SUS_Questionnaire.xlsx file contains the participant responses.

The plugins folder includes the plugin.xml files generated from the task definitions provided by the participants.

The project folder contains the GameScoreManager Java project, which was used to test the new tasks.

```
user_study/
├── plugins/
└── project/
```
## Offline Experiment: Iteration Cost

This experiment is a new evaluation of the iteration cost (latency and tokens) introduced by the improvement cycles in the assistance tasks, in this case applying code completion, and success rates per phase.

The PA21 project was evaluated, using the holistic strategy with maximum context for each execution, with the same workflow: completion, compilation, and validation with JUnit. Up to three iterations per phase were performed. Five executions were performed for each LLM, including gemini-3.5-flash-lite, gpt-5.6luna, and deepseek-v4-flash.

In each execution, using a predefined LLM agent, the results are stored in an "execution-" folder. This folder contains the following files/folders:

- classes.csv: list of results by class.
- classes_summary.csv: summary of results by class.
- iterations.csv: list of results by class and iteration.
- log.txt: execution logs.
- Folders containing the modified projects with each completed class.

The projects_data folder includes the projects used for the evaluation: PA21.

```
iteration_cost
├── projects_data/
└── results/
	├── holistic/
	│   ├── max-context/
```