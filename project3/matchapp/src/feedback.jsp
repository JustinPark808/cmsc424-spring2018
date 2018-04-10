<%@ taglib prefix="tagfiles" tagdir="/WEB-INF/tags" %>

<html>

    <head>
        <meta charset="utf-8"/>
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />

        <link href="css/bootstrap.css" rel="stylesheet" />
        <link href="css/match.css" rel="stylesheet" />

        <title>Feedback</title>
    </head>

    <body>
        <tagfiles:header />

        <div class="container heading">
            <h2>Match == good or match == bad?</h2>
            <form action="feedback.do" method="post">
                <div class="row">
                    <div class="column">
                        Your ID: <br/>
                        <input type="text" size="18" name="id1" required/>
                        <br/>
                        Your Match's ID: <br/>
                        <input type="text" size="18" name="id2" required/>
                        <br/>
                    </div>

                    <div class="column">
                        Were you treated respectfully? <br/>
                        <input type="radio" name="approval" value="1" required>Yes</input>
                        <br/>
                        <input type="radio" name="approval" value="2" required>No</input>
                        <br/>
                    </div>
                </div>

                <div>
                    <input type="submit" value="submit">
                </div>
            </form>
        </div>

        <div class="sample">
            <p>MatchMaker</p>
        </div>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
        <script src="js/bootstrap.min.js"></script>
    </body>
</html>

