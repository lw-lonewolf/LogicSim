package com.logisim.domain;

import com.logisim.data.CircuitDAO;
import com.logisim.data.ProjectDAO;
import com.logisim.domain.components.Component;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Represents a project within the Logisim application.
 * <p>
 * A Project serves as a container for multiple {@link Circuit} instances. It allows
 * users to organize related circuits together. This class also handles high-level
 * persistence operations by delegating to the {@link ProjectDAO}.
 * </p>
 */
public class Project {

    /**
     * The unique identifier for the project, typically assigned by the database.
     */
    private long id;

    /**
     * The display name of the project.
     */
    private String name;

    /**
     * The list of circuits associated with this project.
     */
    private List<Circuit> circuits = new ArrayList<>();

    /**
     * The Data Access Object responsible for persisting project data.
     */
    private ProjectDAO projectdao = new ProjectDAO();

    /**
     * Persists the current project state to the database.
     * <p>
     * This method delegates the saving operation to the internal {@link ProjectDAO} instance.
     * </p>
     */
    public void save() {
        System.out.println("Saving to database...");
        projectdao.saveProject(this);
    }

    /**
     * Loads the project data from the database.
     * <p>
     * This method refreshes the list of circuits associated with this project ID.
     * It uses {@link CircuitDAO} to fetch components AND connections, reconstructing
     * the full logical graph of the circuit.
     * </p>
     */
    public void load() {
        if (this.id == 0) {
            System.out.println("Cannot load: Project ID is not set.");
            return;
        }
        System.out.println("Loading project data for ID: " + this.id);
        CircuitDAO circuitDAO = new CircuitDAO();

        // 1. Get Circuit Metadata
        this.circuits = circuitDAO.getCircuitsByProjectId(this.id);

        // 2. Hydrate each circuit with Components and Connections
        for (Circuit c : this.circuits) {
            // A. Load Components
            List<Component> components = circuitDAO.loadComponents(c.getId());
            c.setComponents(components);

            // Map UUID to Component for connection linking
            Map<String, Component> compMap = new HashMap<>();
            for (Component comp : components) {
                compMap.put(comp.getUuid(), comp);
            }

            // B. Load Connections
            List<CircuitDAO.ConnectionRecord> rawConns =
                circuitDAO.loadConnections(c.getId());
            List<Connector> connectors = new ArrayList<>();

            for (CircuitDAO.ConnectionRecord rec : rawConns) {
                Component source = compMap.get(rec.sourceUuid());
                Component sink = compMap.get(rec.sinkUuid());

                if (source != null && sink != null) {
                    Connector connector = new Connector(
                        source,
                        sink,
                        rec.sourcePin(),
                        rec.sinkPin()
                    );
                    connectors.add(connector);
                }
            }
            c.setConnectors(connectors);
        }
        System.out.println(
            "Project loaded with " + circuits.size() + " circuits."
        );
    }

    /**
     * Exports the project's circuits to JPG image files.
     * <p>
     * This method renders the circuit components and wires onto a {@link BufferedImage}.
     * It simulates the visual layout used in the UI (approx 100x100 components) and
     * draws orthogonal lines for wires.
     * </p>
     */
    public void export() {
        if (circuits.isEmpty()) {
            System.out.println("Nothing to export: No circuits found.");
            return;
        }

        System.out.println(
            "Exporting " + circuits.size() + " circuits to JPG..."
        );

        for (Circuit c : circuits) {
            // Determine canvas size dynamically based on component positions
            int maxX = 1000;
            int maxY = 800;
            for (Component comp : c.getComponents()) {
                maxX = Math.max(maxX, (int) comp.getPositionX() + 200);
                maxY = Math.max(maxY, (int) comp.getPositionY() + 200);
            }

            BufferedImage image = new BufferedImage(
                maxX,
                maxY,
                BufferedImage.TYPE_INT_RGB
            );
            Graphics2D g = image.createGraphics();

            // 1. Draw Background
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, maxX, maxY);

            // Constants matching UI logic
            int COMP_SIZE = 100;
            int PIN_SPACING = 20;

            // 2. Draw Wires (First so they are behind components)
            g.setColor(Color.BLUE);
            g.setStroke(new BasicStroke(3)); // Thicker lines for wires

            for (Connector conn : c.getConnectors()) {
                // Calculate Source Pin Position (Output side - Right)
                Component srcComp = conn.getSourceComp();
                int srcCount = srcComp.getOutputs().length;
                int srcIndex = conn.getSource();

                double srcX = srcComp.getPositionX() + COMP_SIZE + 5; // Right edge
                // Center vertical offset logic matching GateFactory
                double srcYOffset =
                    (srcIndex - (srcCount - 1) / 2.0) * PIN_SPACING;
                double srcY =
                    srcComp.getPositionY() + (COMP_SIZE / 2.0) + srcYOffset;

                // Calculate Sink Pin Position (Input side - Left)
                Component sinkComp = conn.getSinkComp();
                int sinkCount = sinkComp.getInputs().length;
                int sinkIndex = conn.getSink();

                double sinkX = sinkComp.getPositionX() - 5; // Left edge
                double sinkYOffset =
                    (sinkIndex - (sinkCount - 1) / 2.0) * PIN_SPACING;
                double sinkY =
                    sinkComp.getPositionY() + (COMP_SIZE / 2.0) + sinkYOffset;

                // Draw Orthogonal Line (Start -> Mid X -> End)
                int x1 = (int) srcX;
                int y1 = (int) srcY;
                int x2 = (int) sinkX;
                int y2 = (int) sinkY;
                int midX = (x1 + x2) / 2;

                g.drawLine(x1, y1, midX, y1); // Horizontal to mid
                g.drawLine(midX, y1, midX, y2); // Vertical
                g.drawLine(midX, y2, x2, y2); // Horizontal to dest
            }

            // 3. Draw Components
            g.setStroke(new BasicStroke(2));
            for (Component comp : c.getComponents()) {
                int x = (int) comp.getPositionX();
                int y = (int) comp.getPositionY();

                // Component Body
                g.setColor(Color.WHITE);
                g.fillRect(x, y, COMP_SIZE, COMP_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, COMP_SIZE, COMP_SIZE);

                // Component Name Label
                g.drawString(comp.getName().toUpperCase(), x + 30, y + 55);

                // Draw Input Pins (Left)
                int inputCount = comp.getInputs().length;
                for (int i = 0; i < inputCount; i++) {
                    double offset = (i - (inputCount - 1) / 2.0) * PIN_SPACING;
                    int py = (int) (y + (COMP_SIZE / 2.0) + offset);
                    g.setColor(Color.BLUE);
                    g.fillOval(x - 5, py - 3, 6, 6); // Pin circle
                }

                // Draw Output Pins (Right)
                int outputCount = comp.getOutputs().length;
                for (int i = 0; i < outputCount; i++) {
                    double offset = (i - (outputCount - 1) / 2.0) * PIN_SPACING;
                    int py = (int) (y + (COMP_SIZE / 2.0) + offset);
                    g.setColor(Color.BLUE);
                    g.fillOval(x + COMP_SIZE - 1, py - 3, 6, 6);
                }
            }

            g.dispose();

            try {
                // Sanitize filenames
                String safeProjName = this.name.replaceAll(
                    "[^a-zA-Z0-9.-]",
                    "_"
                );
                String safeCircName = c
                    .getName()
                    .replaceAll("[^a-zA-Z0-9.-]", "_");
                String filename = safeProjName + "_" + safeCircName + ".jpg";

                File outputFile = new File(filename);
                ImageIO.write(image, "jpg", outputFile);
                System.out.println("Exported: " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to export circuit: " + c.getName());
                e.printStackTrace();
            }
        }
    }

    /**
     * Constructs a new Project with a specified name.
     *
     * @param name The name to assign to the project.
     */
    public Project(String name) {
        this.name = name;
    }

    /**
     * Constructs a new Project with a specified ID and name.
     * <p>
     * This constructor is typically used when reconstructing a project object
     * from database records.
     * </p>
     *
     * @param id   The database ID of the project.
     * @param name The name of the project.
     */
    public Project(long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Default constructor.
     * <p>
     * Initializes the project with the default name "Project".
     * </p>
     */
    public Project() {
        this("Project");
    }

    /**
     * Retrieves the name of the project.
     *
     * @return The project name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the list of circuits contained in this project.
     *
     * @return A list of {@link Circuit} objects.
     */
    public List<Circuit> getCircuits() {
        return circuits;
    }

    /**
     * Retrieves the unique identifier of the project.
     *
     * @return The project ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the project.
     *
     * @param id The new project ID.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Sets the name of the project.
     *
     * @param name The new project name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the list of circuits for this project.
     *
     * @param circuits The new list of {@link Circuit} objects.
     */
    public void setCircuits(List<Circuit> circuits) {
        this.circuits = circuits;
    }

    /**
     * Retrieves the Data Access Object associated with this project.
     *
     * @return The {@link ProjectDAO} instance.
     */
    public ProjectDAO getProjectdao() {
        return projectdao;
    }

    /**
     * Sets the Data Access Object for this project.
     *
     * @param projectdao The {@link ProjectDAO} instance to be used for persistence.
     */
    public void setProjectdao(ProjectDAO projectdao) {
        this.projectdao = projectdao;
    }

    /**
     * Returns a string representation of the project.
     *
     * @return The name of the project.
     */
    @Override
    public String toString() {
        return name;
    }
}
