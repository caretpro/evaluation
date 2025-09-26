# CARET

[CARET](https://caretpro.github.io/) is a conversational assistant for Java software development, which implements an architecture open, accountable, and trustworthy. This tool is a plugin for the Eclipse IDE that supports code completion, documentation, code optimisation, error fixing and unit testing. It supports different LLM technologies, which can be added through an extension point, and also allows the user to add new tasks and validators.

## CARET Experiment: Code Completion

This experiment is about the evaluation of CARET assistant to complete Java classes for four projects from [JavaBench](https://github.com/java-bench/JavaBench): PA19, PA20, PA21, and PA22. The corresponding solutions are the projects: PA19-Solution, PA20-Solution, PA21-Solution, and PA22-Solution. The execution tests are performed on the projects PA19-Execution, PA20-Execution, PA21-Execution, and PA22-Execution.

Five agents from different technologies were used in the executions: GPT-4.1-mini, GPT-4.1-nano, Codex-mini-latest, Gemini-2.0-flash, and DeepSeek-chat (DeepSeek-V3-0324). 

In each execution, the four projects are evaluated using a predefined LLM agent, and the results are stored in an "execution-" folder. This folder contains the following files/folders:
- classes.csv: a list of results by class.
- log.txt: execution logs.
- xlsx file: summary of the execution results.
- Folders containing the modified projects with each completed class.

The projects_data folder includes the projects used for the evaluation: 
- PA19, PA20, PA21, and PA22: contain incomplete classes.
- -Execution projects: where the executions are performed.
- -Solution projects: contain correctly completed classes.

The results folder includes subfolders grouped by strategy, configuration, and LLM:
```
results/
├── holistic
│   ├── max-context
│   └── min-context
└── sequential
    ├── max-context
    └── min-context
```
