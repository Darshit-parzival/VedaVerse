CharsOnly = commentWithoutAsterisks
                .replace(WHITESPACE, "");

            if (commentWordCharsOnly.length === 0) {
                return true;
            }

            const firstWordChar = commentWordCharsOnly[0];

            if (!LETTER_PATTERN.test(firstWordChar)) {
                return true;
            }

            // 7. Check the case of the initial word character.
            const isUppercase = firstWordChar !== firstWordChar.toLocaleLowerCase(),
                isLowercase = firstWordChar !== firstWordChar.toLocaleUpperCase();

            if (capitalize === "always" && isLowercase) {
                return false;
            }
            if (capitalize === "never" && isUppercase) {
                return false;
            }

            return true;
        }

        /**
         * Process a comment to determine if it needs to be reported.
         * @param {ASTNode} comment The comment node to process.
         * @returns {void}
         */
        function processComment(comment) {
            const options = normalizedOptions[comment.type],
                commentValid = isCommentValid(comment, options);

            if (!commentValid) {
                const messageId = capitalize === "always"
                    ? "unexpectedLowercaseComment"
                    : "unexpectedUppercaseComment";

                context.report({
                    node: null, // Intentionally using loc instead
                    loc: comment.loc,
                    messageId,
                    fix(fixer) {
                        const match = comment.value.match(LETTER_PATTERN);

                        return fixer.replaceTextRange(

                            // Offset match.index by 2 to account for the first 2 characters that start the comment (// or /*)
                            [comment.range[0] + match.index + 2, comment.range[0] + match.index + 3],
                            capitalize === "always" ? match[0].toLocaleUpperCase() : match[0].toLocaleLowerCase()
                        );
                    }
                });
            }
        }

        //----------------------------------------------------------------------
        // Public
        //----------------------------------------------------------------------

        return {
            Program() {
                const comments = sourceCode.getAllComments();

                comments.filter(token => token.type !== "Shebang").forEach(processComment);
            }
        };
    }
};
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                