package shpe.consumer.dao;

import shpe.consumer.model.TokenSet;

import java.io.*;
import java.util.Optional;

public class TokenSetDaoImpl extends TokenSetDao {
    @Override
    public Optional<TokenSet> fetchTokenSet(String tokenSetKey) {

        File tokenSetFile = new File(tokenSetKey);

        if(tokenSetFile.exists()) {
            try (ObjectInputStream tokenSetStream = new ObjectInputStream(new FileInputStream(tokenSetFile))) {
                TokenSet tokenSet = (TokenSet) tokenSetStream.readObject();
                return Optional.of(tokenSet);
            } catch (Exception e) {
                throw new RuntimeException(String.format("Failed while reading token set at location: %s", tokenSetKey), e);
            }
        }
        return Optional.empty();
    }

    @Override
    public void persistTokenSet(String tokenSetKey, TokenSet toBeSaved) {
        File tokenSetFile = createFileIfNessecary(tokenSetKey);
        saveTokenSetToFile(toBeSaved, tokenSetFile);
    }

    private void saveTokenSetToFile(TokenSet toBeSaved, File tokenSetFile) {
        try (ObjectOutputStream tokenSetStream = new ObjectOutputStream(new FileOutputStream(tokenSetFile))) {
            tokenSetStream.writeObject(toBeSaved);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed while saving tokenSet to location: %s"
                    , tokenSetFile.getAbsolutePath()), e);
        }
    }

    private File createFileIfNessecary(String tokenSetKey) {
        File tokenSetFile = new File(tokenSetKey);
        try {
            if (!tokenSetFile.exists()) {
                tokenSetFile.createNewFile();
            }
            return tokenSetFile;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed while creating file at path: %s", tokenSetKey), e);
        }
    }
}
